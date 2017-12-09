package tracker;

import requests.TrackerRequest;
import requests.TrackerRequestFactory;

import java.io.IOException;
import java.util.*;

public class TrackerWorker implements Runnable {
    private final Peer peer;
    private final Map<Integer, Set<Peer>> index;

    public TrackerWorker(Peer peer, Map<Integer, Set<Peer>> index) {
        this.peer = peer;
        this.index = index;
    }

    @Override
    public void run() {
        while (true) {
            if (peer.getSocket().isClosed()) {
                return;
            }
            try {
                TrackerRequest request = TrackerRequestFactory.parseRequest(peer.getSocket().getInputStream());
                request.execute(peer.getSocket().getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
