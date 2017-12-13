package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SourcesRequestCallback implements TrackerRequestCallback {
    private final SourcesRequest request;

    public SourcesRequestCallback(SourcesRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        synchronized (environment) {
            Set<Peer> peers = environment.getPeers(request.getFileId());
            List<HostPort> sources = peers.stream().map(Peer::getHostPort).collect(Collectors.toList());
            SourcesResponse response = new SourcesResponse(sources);
            response.dump(peer.getSocket().getOutputStream());
        }
    }
}
