package requests;

import org.jetbrains.annotations.NotNull;
import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SourcesRequestCallback implements TrackerRequestCallback {
    @NotNull
    private final SourcesRequest request;

    public SourcesRequestCallback(@NotNull SourcesRequest request) {
        this.request = request;
    }

    @Override
    public void execute(@NotNull Socket socket, @NotNull TrackerEnvironment environment) throws IOException {
        Set<Peer> peers = environment.getPeers(request.getFileId());
        List<HostPort> sources = peers.stream()
                .map(Peer::getHostPort)
                .filter(hp -> hp.getPort() > 0)
                .collect(Collectors.toList());

        SourcesResponse response = new SourcesResponse(sources);
        response.dump(socket.getOutputStream());
    }
}
