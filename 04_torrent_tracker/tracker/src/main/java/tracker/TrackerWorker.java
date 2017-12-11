package tracker;

import requests.TrackerRequestCallback;
import requests.TrackerRequestCallbackFactory;

import java.io.IOException;

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
                break;
            }
            try {
                TrackerRequestCallback callback = TrackerRequestCallbackFactory.parseRequest(peer.getSocket().getInputStream());
                callback.execute(peer, environment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
