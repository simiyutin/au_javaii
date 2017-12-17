package requests;

import org.jetbrains.annotations.NotNull;

import java.io.*;

import static requests.RequestType.UPLOAD;

public class UploadRequest {
    private final RequestType type = UPLOAD;
    @NotNull
    private final String name;
    private final long size;

    public UploadRequest(@NotNull String name, long size) {
        this.name = name;
        this.size = size;
    }

    public RequestType getType() {
        return type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    @NotNull
    public static UploadRequest parse(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        String name = dis.readUTF();
        long size = dis.readLong();
        return new UploadRequest(name, size);
    }

    public void dump(@NotNull OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeUTF(getName());
        dos.writeLong(getSize());
    }
}
