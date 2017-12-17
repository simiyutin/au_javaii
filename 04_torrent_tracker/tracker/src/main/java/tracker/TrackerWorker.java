package tracker;

import org.jetbrains.annotations.NotNull;
import requests.TrackerRequestCallback;
import requests.TrackerRequestCallbackFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TrackerWorker implements Runnable {
    @NotNull
    private final Socket socket;
    @NotNull
    private final TrackerEnvironment environment;

    public TrackerWorker(@NotNull Socket socket, @NotNull TrackerEnvironment environment) {
        this.socket = socket;
        this.environment = environment;
    }

    @Override
    public void run() {
        while (true) {
            if (socket.isClosed()) {
                return;
            }
            try {
                TrackerRequestCallback callback = TrackerRequestCallbackFactory.parseRequest(socket.getInputStream());
                callback.execute(socket, environment);
            } catch (EOFException | SocketException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
