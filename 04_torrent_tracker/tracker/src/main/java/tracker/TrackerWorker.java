package tracker;

import requests.TrackerRequest;
import requests.TrackerRequestFactory;

import java.io.IOException;
import java.util.*;

public class TrackerWorker implements Runnable {
    private final Peer peer;
    private final TrackerEnvironment environment;

    public TrackerWorker(Peer peer, TrackerEnvironment environment) {
        this.peer = peer;
        this.environment = environment;
    }

    @Override
    public void run() {
        while (true) {
            if (peer.getSocket().isClosed()) {
                return;
            }
            try {
                TrackerRequest request = TrackerRequestFactory.parseRequest(peer.getSocket().getInputStream());
                request.execute(peer.getSocket().getOutputStream(), environment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
