package requests;

import tracker.TrackerEnvironment;

import java.io.IOException;
import java.io.OutputStream;

public interface TrackerRequest {
    void execute(OutputStream os, TrackerEnvironment environment) throws IOException;
}
