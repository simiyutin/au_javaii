package requests;

import org.jetbrains.annotations.NotNull;

import java.io.*;

public class UploadResponse {
    private final int id;

    public UploadResponse(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public static UploadResponse parse(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int id = dis.readInt();
        return new UploadResponse(id);
    }

    public void dump(@NotNull OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getId());
    }
}
