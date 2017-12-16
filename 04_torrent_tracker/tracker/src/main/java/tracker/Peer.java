package tracker;

import requests.HostPort;

import java.net.Socket;
import java.util.Objects;

public class Peer {
    private volatile long lastUpdate;
    private final HostPort hostPort;
    public static final long MAX_LIFE_MINS = 5;

    public Peer(HostPort hostPort) {
        this.hostPort = hostPort;
        this.lastUpdate = System.currentTimeMillis();
    }

    public boolean outdated() {
        long diff = System.currentTimeMillis() - lastUpdate;
        long maxdiff = MAX_LIFE_MINS * 60 * 1000;
        return diff > maxdiff;
    }

    public HostPort getHostPort() {
        return hostPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return Objects.equals(hostPort, peer.hostPort);
    }

    @Override
    public int hashCode() {

        return Objects.hash(hostPort);
    }
}
