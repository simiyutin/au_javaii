package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UploadRequestCallback implements TrackerRequestCallback {
    private final UploadRequest request;

    public UploadRequestCallback(UploadRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Socket socket, TrackerEnvironment environment) throws IOException {
        int fileId = environment.addFile(request.getName(), request.getSize());
        UploadResponse response = new UploadResponse(fileId);
        response.dump(socket.getOutputStream());
    }
}
