package client;

import java.net.Socket;

public class Leech {
    private final Socket socket;

    public Leech(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
