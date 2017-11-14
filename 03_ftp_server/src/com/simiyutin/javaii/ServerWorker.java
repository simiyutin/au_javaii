package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;
import org.jetbrains.annotations.NotNull;

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
            if (socket.isClosed()) {
                System.out.println("server: socket is closed");
                return;
            }
            try {
                Pair<RequestType, String> request = RequestFactory.parseRequest(socket.getInputStream());
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

    @NotNull
    private List<Pair<String, Boolean>> getListOfDir(@NotNull String path) {
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

    @NotNull
    //byte array instead of stream assuming file size will be less than 2gb
    private byte [] getFileBytes(@NotNull String fileName) throws IOException {
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
