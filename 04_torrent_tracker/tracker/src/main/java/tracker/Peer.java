package tracker;

import java.net.Socket;

public class Peer {
    private volatile long lastUpdate;
    private final Socket socket;
    public static final long MAX_LIFE_MINS = 5;

    public Peer(Socket socket) {
        this.socket = socket;
        update();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public boolean outdated() {
        long diff = System.currentTimeMillis() - lastUpdate;
        long maxdiff = MAX_LIFE_MINS * 60 * 1000;
        return diff > maxdiff;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getPort() {
        return socket.getPort();
    }

    public byte[] getIp() {
        return socket.getInetAddress().getAddress();
    }

    public void update() {
        lastUpdate = System.currentTimeMillis();
    }
}
