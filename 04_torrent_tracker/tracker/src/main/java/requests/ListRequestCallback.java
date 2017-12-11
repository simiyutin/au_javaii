package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ListRequestCallback implements TrackerRequestCallback {

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        synchronized (environment) {
            ListResponse response = new ListResponse(environment.getIndex().keySet());
            response.dump(peer.getSocket().getOutputStream());
        }
    }
}
