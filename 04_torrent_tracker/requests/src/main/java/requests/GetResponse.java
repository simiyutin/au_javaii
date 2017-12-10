package requests;

import java.io.*;

public class GetResponse {
    private final byte[] bytes;

    public GetResponse(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getSize() {
        return bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static GetResponse parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int size = dis.readInt();
        byte bytes[] = new byte[size];
        dis.read(bytes, 0, size);
        return new GetResponse(bytes);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getSize());
        dos.write(getBytes(), 0, getSize());
    }
}
