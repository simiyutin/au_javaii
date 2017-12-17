package requests;

import org.jetbrains.annotations.NotNull;
import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.IOException;
import java.net.Socket;

public class UpdateRequestCallback implements TrackerRequestCallback {
    @NotNull
    private final UpdateRequest request;

    public UpdateRequestCallback(@NotNull UpdateRequest request) {
        this.request = request;
    }

    @Override
    public void execute(@NotNull Socket socket, @NotNull TrackerEnvironment environment) throws IOException {
        HostPort hostPort = new HostPort(socket.getInetAddress().getAddress(), request.getClientPort());
        Peer newPeer = new Peer(hostPort);
        environment.updatePeer(newPeer);
        for (Integer fileId : request.getFileIds()) {
            environment.updatePeerForFile(newPeer, fileId);
        }

        UpdateResponse response = new UpdateResponse(true);
        response.dump(socket.getOutputStream());
    }
}
