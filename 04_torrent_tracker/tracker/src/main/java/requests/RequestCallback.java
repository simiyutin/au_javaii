package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.IOException;

public interface RequestCallback {
    void execute(Peer peer, TrackerEnvironment environment) throws IOException;
}
