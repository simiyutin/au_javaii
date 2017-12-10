package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateRequestCallback implements RequestCallback {
    private final UpdateRequest request;

    public UpdateRequestCallback(UpdateRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        peer.update();
        DataOutputStream dis = new DataOutputStream(peer.getSocket().getOutputStream());
        dis.writeBoolean(true);
    }
}
