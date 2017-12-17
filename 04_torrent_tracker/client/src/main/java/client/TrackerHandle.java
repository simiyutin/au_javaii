package client;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public class TrackerHandle {
    @NotNull
    private final String trackerHost;
    private final int trackerPort;

    public TrackerHandle(@NotNull String trackerHost, int trackerPort) {
        this.trackerHost = trackerHost;
        this.trackerPort = trackerPort;
    }

    @NotNull
    public Socket getNewSocket() throws IOException {
        return new Socket(trackerHost, trackerPort);
    }
}
