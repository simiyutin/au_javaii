package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class SourcesRequestCallback implements RequestCallback {
    private final SourcesRequest request;

    public SourcesRequestCallback(SourcesRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        synchronized (environment) {
            Set<Peer> peers = environment.getPeers(request.getFileId());
            DataOutputStream dos = new DataOutputStream(peer.getSocket().getOutputStream());
            dos.writeInt(peers.size());
            for (Peer filePeer : peers) {
                dos.write(filePeer.getIp());
                if (filePeer.getIp().length != 4) {
                    throw new RuntimeException("lol");
                }
                dos.writeInt(filePeer.getPort());
            }
        }
    }
}
