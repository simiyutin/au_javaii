package requests;

import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.IOException;

public interface TrackerRequestCallback {
    void execute(Peer peer, TrackerEnvironment environment) throws IOException;
}
