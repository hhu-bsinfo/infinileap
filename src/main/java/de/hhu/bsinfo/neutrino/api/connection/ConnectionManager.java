package de.hhu.bsinfo.neutrino.api.connection;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final ConnectionInfo connectionInfo;

    public ConnectionManager(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public Observable<ConnectionInfo> listen(final InetSocketAddress bindAddress) {
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
                            clientKey.attach(new Handler(client, clientKey, connectionInfo));
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

    public Single<ConnectionInfo> connect(final InetSocketAddress serverAddress) {
        return Single.fromCallable(() -> {
           try (var channel = SocketChannel.open(serverAddress)) {
               ByteBuffer buffer = ByteBuffer.allocateDirect(Short.BYTES + Integer.BYTES);
               buffer.putShort(connectionInfo.getLocalId())
                     .putInt(connectionInfo.getQueuePairNumber())
                     .flip();

               channel.write(buffer);
               buffer.flip();
               channel.read(buffer);
               buffer.flip();

               return new ConnectionInfo(buffer);
           }
        });
    }

    private static final class Handler {

        private static final int CONNECTION_SIZE = Short.BYTES + Integer.BYTES;

        private final ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(CONNECTION_SIZE);
        private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(CONNECTION_SIZE);

        private final SocketChannel socketChannel;
        private final SelectionKey selectionKey;

        Handler(SocketChannel socketChannel, SelectionKey selectionKey, ConnectionInfo connectionInfo) {
            this.socketChannel = socketChannel;
            this.selectionKey = selectionKey;

            sendBuffer.putShort(connectionInfo.getLocalId());
            sendBuffer.putInt(connectionInfo.getQueuePairNumber());
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

        public ConnectionInfo getConnection() {
            return new ConnectionInfo(receiveBuffer.flip());
        }

        boolean isFinished() {
            return !receiveBuffer.hasRemaining() && !sendBuffer.hasRemaining();
        }

        void cancel() {
            selectionKey.cancel();
        }
    }
}
