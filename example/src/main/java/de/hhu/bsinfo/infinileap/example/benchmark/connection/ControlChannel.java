package de.hhu.bsinfo.infinileap.example.benchmark.connection;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ControlChannel {

    public enum Action {
        SET_THREAD_COUNT(0x01),
        START_RUN(0x02),
        FINISH_RUN(0x03),
        SHUTDOWN(0x04);


        private final int value;

        Action(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Action from(int value) {
            return Arrays.stream(values())
                    .filter(action -> action.value() == value)
                    .findFirst()
                    .orElseThrow();
        }
    }

    private final Socket control;

    private final DataInputStream in;
    private final DataOutputStream out;

    private ControlChannel(Socket socket) throws IOException {
        this.control = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public static ControlChannel listen(InetSocketAddress address) throws IOException {
        try (var serverSocket = new ServerSocket(address.getPort(), 0, address.getAddress())) {
            return new ControlChannel(serverSocket.accept());
        }
    }

    public static ControlChannel connect(InetSocketAddress address) throws IOException {
        return new ControlChannel(new Socket(address.getHostName(), address.getPort()));
    }

    public void sendAction(Action action) throws IOException {
        out.writeInt(action.value);
    }

    public Action receiveAction() throws IOException {
        return Action.from(in.readInt());
    }

    public void sendThreadCount(int threadCount) throws IOException {
        sendAction(Action.SET_THREAD_COUNT);
        out.writeInt(threadCount);
    }

    public int receiveThreadCount() throws IOException {
        Action action;
        if ((action = receiveAction()) != Action.SET_THREAD_COUNT) {
            throw new RuntimeException("Received unexpected action " + action.name());
        }

        return in.readInt();
    }
}
