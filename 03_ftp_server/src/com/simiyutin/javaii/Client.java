package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class Client {

    private Socket socket = null;

    public void connect(@NotNull String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public void disconnect() throws IOException {
        if (socket == null) {
            throw new RuntimeException("Client is not connected to server");
        }
        socket.close();
    }

    @NotNull
    public List<Pair<String, Boolean>> executeList(@NotNull String dir) throws IOException {
        if (socket == null) {
            throw new RuntimeException("Client is not connected to server");
        }
        byte [] request = RequestFactory.createListRequest(dir);
        socket.getOutputStream().write(request);
        InputStream is = socket.getInputStream();
        return RequestFactory.parseListResponse(is);
    }

    @NotNull
    public byte [] executeGet(@NotNull String file) throws IOException {
        if (socket == null) {
            throw new RuntimeException("Client is not connected to server");
        }
        byte [] request = RequestFactory.createGetRequest(file);
        socket.getOutputStream().write(request);
        InputStream is = socket.getInputStream();
        return RequestFactory.parseGetResponse(is);
    }
}
