package tracker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Tracker {
    private ServerSocket serverSocket = null;
    private final List<Peer> peers = new ArrayList<>();
    private final Map<Integer, Set<Peer>> index = new HashMap<>();
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
                    synchronized (peers) {
                        peers.add(peer);
                    }
                    new Thread(new TrackerWorker(peer, index)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void stop() {

    }
}
