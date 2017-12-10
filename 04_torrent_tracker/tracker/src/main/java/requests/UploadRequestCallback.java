package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

public class UploadRequestCallback implements RequestCallback {
    private final UploadRequest request;

    public UploadRequestCallback(UploadRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        synchronized (environment) {
            int fileId = environment.addFile(peer, request.getName(), request.getSize());
            DataOutputStream dos = new DataOutputStream(peer.getSocket().getOutputStream());
            dos.writeInt(fileId);
        }
    }
}
