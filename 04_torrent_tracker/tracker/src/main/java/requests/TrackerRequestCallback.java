package requests;

import org.jetbrains.annotations.NotNull;
import tracker.TrackerEnvironment;

import java.io.IOException;
import java.net.Socket;

public interface TrackerRequestCallback {
    void execute(@NotNull Socket socket, @NotNull TrackerEnvironment environment) throws IOException;
}
