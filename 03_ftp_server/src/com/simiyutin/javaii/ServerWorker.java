package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;

import java.io.*;
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
        File dir = new File(path);
        File [] files = dir.listFiles();
        if (files == null) {
            return result;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                result.add(new Pair<>(f.getName(), true));
            } else {
                result.add(new Pair<>(f.getName(), false));
            }
        }
        return result;
    }

    //byte array instead of stream assuming file size will be less than 2gb
    private byte [] getFileBytes(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int bufferSize = 1024;
        byte [] buffer = new byte[bufferSize];
        int readSize;
        while ((readSize = bis.read(buffer, 0, bufferSize)) > 0) {
            baos.write(buffer, 0, readSize);
        }
        return baos.toByteArray();
    }
}
