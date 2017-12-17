package requests;

import org.jetbrains.annotations.NotNull;
import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UploadRequestCallback implements TrackerRequestCallback {
    @NotNull
    private final UploadRequest request;

    public UploadRequestCallback(@NotNull UploadRequest request) {
        this.request = request;
    }

    @Override
    public void execute(@NotNull Socket socket, @NotNull TrackerEnvironment environment) throws IOException {
        int fileId = environment.addFile(request.getName(), request.getSize());
        UploadResponse response = new UploadResponse(fileId);
        response.dump(socket.getOutputStream());
    }
}
