package requests;

import tracker.TrackerEnvironment;

import java.io.IOException;
import java.net.Socket;

public interface TrackerRequestCallback {
    void execute(Socket socket, TrackerEnvironment environment) throws IOException;
}
