package requests;

import java.io.*;

import static requests.RequestType.SOURCES;

public class SourcesRequest {
    private final RequestType type = SOURCES;
    private final int fileId;

    public SourcesRequest(int fileId) {
        this.fileId = fileId;
    }

    public RequestType getType() {
        return type;
    }

    public int getFileId() {
        return fileId;
    }

    public static SourcesRequest parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int fileId = dis.readInt();
        return new SourcesRequest(fileId);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeInt(getFileId());
    }
}
