package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

public class TrackerListRequest implements TrackerRequest {

    @Override
    public void execute(OutputStream os, TrackerEnvironment environment) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(environment.getIndex().keySet().size());
        for (Map.Entry<TrackerEnvironment.FileInfo, Set<Peer>> entry : environment.getIndex().entrySet()){
            dos.writeInt(entry.getKey().getId());
            dos.writeUTF(entry.getKey().getName());
            dos.writeLong(entry.getKey().getSize());
        }
    }
}
