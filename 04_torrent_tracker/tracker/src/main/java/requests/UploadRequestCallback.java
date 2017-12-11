package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;

public class UploadRequestCallback implements TrackerRequestCallback {
    private final UploadRequest request;

    public UploadRequestCallback(UploadRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        synchronized (environment) {
            int fileId = environment.addFile(peer, request.getName(), request.getSize());
            UploadResponse response = new UploadResponse(fileId);
            response.dump(peer.getSocket().getOutputStream());
        }
    }
}
