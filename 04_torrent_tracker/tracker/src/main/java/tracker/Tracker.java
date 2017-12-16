package tracker;

import requests.FileInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Tracker {
    private ServerSocket serverSocket = null;
    private final List<Socket> sockets = new ArrayList<>();
    private final TrackerEnvironment environment = new TrackerEnvironment();
    private final int PORT = 8081;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        startListenerThread();
        startReaperThread();
    }

    private void startListenerThread() {
        new Thread(() -> {
            try {
                while (true) {
                    System.out.println("tracker: waiting for client");
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
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
