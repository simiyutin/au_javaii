package com.simiyutin.javaii;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<Socket> sockets = new ArrayList<>();

    public void start(int port) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket socket = ss.accept();
            sockets.add(socket);
            new Thread(new ServerWorker(socket)).start();
        }
    }

    public void stop() {
        sockets.forEach(s -> {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
