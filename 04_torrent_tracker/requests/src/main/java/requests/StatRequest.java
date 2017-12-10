package requests;

import java.io.*;

import static requests.RequestType.STAT;

public class StatRequest {
    private final RequestType type = STAT;
    private final int fileId;

    public StatRequest(int fileId) {
        this.fileId = fileId;
    }

    public RequestType getType() {
        return type;
    }

    public int getFileId() {
        return fileId;
    }

    public static StatRequest parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int id = dis.readInt();
        return new StatRequest(id);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeInt(getFileId());
    }
}
