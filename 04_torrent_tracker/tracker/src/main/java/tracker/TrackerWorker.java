package tracker;

import requests.RequestCallback;
import requests.TrackerRequestFactory;

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
                RequestCallback callback = TrackerRequestFactory.parseRequest(peer.getSocket().getInputStream());
                callback.execute(peer, environment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
