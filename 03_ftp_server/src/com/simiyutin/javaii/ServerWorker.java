package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerWorker implements Runnable {
    private final Socket socket;

    public ServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try { //todo try with resources
                InputStream is = socket.getInputStream();
                Pair<RequestType, String> request = RequestFactory.parseRequest(is);
                switch (request.fst) {
                    case LIST: {
                        List<Pair<String, Boolean>> result = getListOfDir(request.snd);
                        byte[] response = RequestFactory.createListResponse(result);
                        socket.getOutputStream().write(response);
                        break;
                    }
                    case GET: {
                        byte[] result = getFileBytes(request.snd);
                        byte[] response = RequestFactory.createGetResponse(result);
                        socket.getOutputStream().write(response);
                        break;
                    }
                    default:
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private List<Pair<String, Boolean>> getListOfDir(String path) {
        List<Pair<String, Boolean>> result = new ArrayList<>();
        result.add(new Pair<>("dir1", true));
        result.add(new Pair<>("dir2", true));
        result.add(new Pair<>("dir3", true));
        result.add(new Pair<>("file1", false));
        result.add(new Pair<>("file2", false));
        return result;
    }

    //byte array instead of stream assumning file size will be less than 2gb
    private byte [] getFileBytes(String file) {
        return "test file content".getBytes();
    }
}
