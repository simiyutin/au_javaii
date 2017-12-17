package client;

import java.io.IOException;
import java.net.Socket;

public class TrackerHandle {
    private final String trackerHost;
    private final int trackerPort;

    public TrackerHandle(String trackerHost, int trackerPort) {
        this.trackerHost = trackerHost;
        this.trackerPort = trackerPort;
    }

    public TrackerHandle() {
        this.trackerHost = null;
        this.trackerPort = 0;
    }

    //    public Socket getSocket() throws IOException {
//        return new Socket(trackerHost, trackerPort);
//    }

    public Socket getNewSocket() throws IOException {
        if (trackerHost == null) {
            throw new IOException("uninitialized tracker host");
        }
        return new Socket(trackerHost, trackerPort);
    }
}
