package requests;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public static StatRequest parse(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int id = dis.readInt();
        return new StatRequest(id);
    }

    public void dump(@NotNull OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeInt(getFileId());
    }
}
