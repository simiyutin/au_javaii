package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateRequestCallback implements TrackerRequestCallback {
    private final UpdateRequest request;

    public UpdateRequestCallback(UpdateRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        peer.update();
        // todo
        UpdateResponse response = new UpdateResponse(true);
        response.dump(peer.getSocket().getOutputStream());
    }
}
