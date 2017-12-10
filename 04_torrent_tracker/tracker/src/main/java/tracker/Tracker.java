package tracker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Tracker {
    private ServerSocket serverSocket = null;
    private final TrackerEnvironment environment = new TrackerEnvironment();
    private final int PORT = 8081;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        startListenerThread();
    }

    private void startListenerThread() throws IOException {
        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    Peer peer = new Peer(socket);
                    synchronized (environment.getPeers()) {
                        environment.getPeers().add(peer);
                    }
                    new Thread(new TrackerWorker(peer, environment)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void stop() {

    }
}
