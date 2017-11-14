package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.simiyutin.javaii.RequestType.GET;
import static com.simiyutin.javaii.RequestType.LIST;

public class RequestFactory {

    @NotNull
    public static byte [] createListRequest(@NotNull String path) {
        return createRequest(LIST, path);
    }

    @NotNull
    public static byte [] createGetRequest(@NotNull String path) {
        return createRequest(GET, path);
    }

    @NotNull
    private static byte [] createRequest(@NotNull RequestType type, @NotNull String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(type.getValue());
            dos.writeUTF(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    @NotNull
    public static Pair<RequestType, String> parseRequest(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int request = dis.readInt();
        String path = dis.readUTF();
        return new Pair<>(RequestType.valueOf(request), path);
    }

    @NotNull
    public static List<Pair<String, Boolean>> parseListResponse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int size = dis.readInt();
        List<Pair<String, Boolean>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String path = dis.readUTF();
            boolean isDir = dis.readBoolean();
            result.add(new Pair<>(path, isDir));
        }
        return result;
    }

    @NotNull
    public static byte [] parseGetResponse(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        //int instead of long assuming file size will be less than 2 gb
        int size = dis.readInt();
        byte [] result = new byte[size];
        dis.read(result);
        return result;
    }

    @NotNull
    public static byte [] createListResponse(@NotNull List<Pair<String, Boolean>> dirs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(dirs.size());
            for (Pair<String, Boolean> p : dirs){
                String dir = p.fst;
                boolean dirFlag = p.snd;
                dos.writeUTF(dir);
                dos.writeBoolean(dirFlag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    @NotNull
    public static byte [] createGetResponse(@NotNull byte[] fileContent) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            //int instead of long assuming file size will be less than 2 gb
            dos.writeInt(fileContent.length);
            dos.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

}
