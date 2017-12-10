package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ListRequestCallback implements RequestCallback {

    @Override
    public void execute(Peer peer, TrackerEnvironment environment) throws IOException {
        synchronized (environment) { //todo create response and then serialize
            DataOutputStream dos = new DataOutputStream(peer.getSocket().getOutputStream());
            dos.writeInt(environment.getIndex().keySet().size());
            for (Map.Entry<FileInfo, Set<Peer>> entry : environment.getIndex().entrySet()){
                dos.writeInt(entry.getKey().getId());
                dos.writeUTF(entry.getKey().getName());
                dos.writeLong(entry.getKey().getSize());
            }
        }
    }
}
