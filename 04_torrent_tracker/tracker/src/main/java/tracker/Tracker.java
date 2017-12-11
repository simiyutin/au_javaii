package tracker;

import requests.FileInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Tracker {
    private ServerSocket serverSocket = null;
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
                    Peer peer = new Peer(socket);
                    synchronized (environment) {
                        environment.getPeers().add(peer);
                    }
                    new Thread(new TrackerWorker(peer, environment)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                            try {
                                peer.getSocket().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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

    }
}
