package de.hhu.bsinfo.neutrino.api.connection;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.LockSupport;

public class ConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final Connection connection;

    public ConnectionManager(Connection connection) {
        this.connection = connection;
    }

    public Observable<Connection> listen(final InetSocketAddress bindAddress) {
        return Observable.create( emitter -> {
            try (var selector = Selector.open();
                 var serverChannel = ServerSocketChannel.open().bind(bindAddress)) {

                serverChannel.configureBlocking(false);
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);

                while (!emitter.isDisposed()) {
                    selector.select();
                    var selectedKeys = selector.selectedKeys();
                    var iterator = selectedKeys.iterator();
                    while(iterator.hasNext()) {
                        var key = iterator.next();
                        var handler = (Handler) key.attachment();

                        if (key.isAcceptable()) {
                            var client = serverChannel.accept();
                            client.configureBlocking(false);
                            var clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            clientKey.attach(new Handler(client, clientKey, connection));
                        }

                        if (handler != null && key.isReadable()) {
                            handler.read();
                        }

                        if (handler != null && key.isWritable()) {
                            handler.write();
                        }

                        if (handler != null && handler.isFinished()) {
                            handler.cancel();
                            emitter.onNext(handler.getConnection());
                        }

                        iterator.remove();
                    }
                }
            }
        });
    }

    public Single<Connection> connect(final InetSocketAddress serverAddress) {
        return Single.fromCallable(() -> {
           try (var channel = SocketChannel.open(serverAddress)) {
               ByteBuffer buffer = ByteBuffer.allocateDirect(Short.BYTES + Integer.BYTES);
               buffer.putShort(connection.getLocalId())
                     .putInt(connection.getQueuePairNumber())
                     .flip();

               channel.write(buffer);
               buffer.flip();
               channel.read(buffer);
               buffer.flip();

               return new Connection(buffer);
           }
        });
    }

    private static final class Handler {

        private static final int CONNECTION_SIZE = Short.BYTES + Integer.BYTES;

        private final ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(CONNECTION_SIZE);
        private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(CONNECTION_SIZE);

        private final SocketChannel socketChannel;
        private final SelectionKey selectionKey;

        Handler(SocketChannel socketChannel, SelectionKey selectionKey, Connection connection) {
            this.socketChannel = socketChannel;
            this.selectionKey = selectionKey;

            sendBuffer.putShort(connection.getLocalId());
            sendBuffer.putInt(connection.getQueuePairNumber());
            sendBuffer.flip();
        }

        void read() throws IOException {
            if (receiveBuffer.hasRemaining()) {
                socketChannel.read(receiveBuffer);
            }

            selectionKey.interestOpsAnd(~SelectionKey.OP_READ);
        }

        void write() throws IOException {
            if (sendBuffer.hasRemaining()) {
                socketChannel.write(sendBuffer);
            }

            selectionKey.interestOpsAnd(~SelectionKey.OP_WRITE);
        }

        public Connection getConnection() {
            return new Connection(receiveBuffer.flip());
        }

        boolean isFinished() {
            return !receiveBuffer.hasRemaining() && !sendBuffer.hasRemaining();
        }

        void cancel() {
            selectionKey.cancel();
        }
    }
}
