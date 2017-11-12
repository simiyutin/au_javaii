package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class Client {

    private Socket socket = null;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public void disconnect() {
        if (socket == null) {
            throw new RuntimeException("Client is not connected to server");
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<Pair<String, Boolean>> executeList(String dir) throws IOException {
        if (socket == null) {
            throw new RuntimeException("Client is not connected to server");
        }
        byte [] request = RequestFactory.createListRequest(dir);
        socket.getOutputStream().write(request);
        InputStream is = socket.getInputStream();
        return RequestFactory.parseListResponse(is);
    }

    byte [] executeGet(String file) throws IOException {
        if (socket == null) {
            throw new RuntimeException("Client is not connected to server");
        }
        byte [] request = RequestFactory.createGetRequest(file);
        socket.getOutputStream().write(request);
        InputStream is = socket.getInputStream();
        return RequestFactory.parseGetResponse(is);
    }
}
