package requests;

import org.jetbrains.annotations.NotNull;
import tracker.Peer;
import tracker.TrackerEnvironment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class ListRequestCallback implements TrackerRequestCallback {

    @Override
    public void execute(@NotNull Socket socket, @NotNull TrackerEnvironment environment) throws IOException {
        ListResponse response = new ListResponse(environment.getIndex().keySet());
        response.dump(socket.getOutputStream());
    }
}
