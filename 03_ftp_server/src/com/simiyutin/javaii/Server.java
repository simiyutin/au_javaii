package com.simiyutin.javaii;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private final List<Socket> sockets = new ArrayList<>();
    private volatile IOException thrownEx = null;
    private ServerSocket ss = null;

    public void start(int port) throws IOException {
        ss = new ServerSocket(port);
        new Thread(() -> {
            List<Thread> workers = new ArrayList<>();
            try {
                while (!isStopped()) {
                    Socket socket = ss.accept();
                    synchronized (sockets) {
                        sockets.add(socket);
                    }
                    workers.add(new Thread(new ServerWorker(socket)));
                    workers.get(workers.size() - 1).start();
                }
            } catch (IOException ex) {
                this.thrownEx = ex;
            }
        }).start();
    }

    public boolean isStopped() {
        return ss == null;
    }

    public void stop() throws IOException {
        if (ss == null) {
            throw new RuntimeException("server is not yet started");
        }
        ss.close();
        ss = null;
        synchronized (sockets) {
            IOException ex = thrownEx;
            for (Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    if (ex == null) {
                        ex = e;
                    } else {
                        ex.addSuppressed(e);
                    }
                }
            }
            if (ex != null) {
                throw ex;
            }
        }

    }
}
