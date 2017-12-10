package requests;

import java.io.*;

import static requests.RequestType.UPLOAD;

public class UploadRequest {
    private final RequestType type = UPLOAD;
    private final String name;
    private final long size;

    public UploadRequest(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public RequestType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public static UploadRequest parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        String name = dis.readUTF();
        long size = dis.readLong();
        return new UploadRequest(name, size);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeUTF(getName());
        dos.writeLong(getSize());
    }
}
