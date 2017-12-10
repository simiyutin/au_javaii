package requests;

import java.io.*;

public class UpdateResponse {
    private final boolean status;

    public UpdateResponse(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public static UpdateResponse parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        boolean status = dis.readBoolean();
        return new UpdateResponse(status);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeBoolean(getStatus());
    }
}
