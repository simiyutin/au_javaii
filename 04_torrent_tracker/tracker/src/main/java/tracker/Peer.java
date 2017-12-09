package tracker;

import java.net.Socket;

public class Peer {
    private long lastUpdate;
    private final Socket socket;

    public Peer(Socket socket) {
        this.socket = socket;
        update();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public Socket getSocket() {
        return socket;
    }

    public void update() {
        lastUpdate = System.currentTimeMillis();
    }
}
