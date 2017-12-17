package tracker;

import requests.FileInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Tracker {
    private final ServerSocket serverSocket;
    private final List<Socket> sockets;
    private final TrackerEnvironment environment;
    private final int PORT = 8081;

    public Tracker(String trackerIndexPath) throws IOException {
        this.environment = new TrackerEnvironment(trackerIndexPath);
        this.serverSocket = new ServerSocket(PORT);
        this.sockets = new ArrayList<>();
        startListenerThread();
        startReaperThread();
    }

    private void startListenerThread() {
        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    synchronized (sockets) {
                        sockets.add(socket);
                    }
                    new Thread(new TrackerWorker(socket, environment)).start();
                }
            } catch (IOException e) {

            }

        }).start();
    }

    private void startReaperThread() {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(Peer.MAX_LIFE_MINS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (environment) {
                    Iterator<Peer> it = environment.getPeers().iterator();
                    Set<Peer> outdated = new HashSet<>();
                    while (it.hasNext()) {
                        Peer peer = it.next();
                        if (peer.outdated()) {
                            it.remove();
                            outdated.add(peer);
                        }
                    }
                    for (Map.Entry<FileInfo, Set<Peer>> entry : environment.getIndex().entrySet()){
                        entry.getValue().removeAll(outdated);
                    }
                }
            }
        });
    }

    public void stop() {
        synchronized (sockets) {
            for (Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
