package requests;

import org.jetbrains.annotations.NotNull;

import java.io.*;

import static requests.RequestType.GET;

public class GetRequest {
    private final RequestType type = GET;
    private final int id;
    private final int part;

    public GetRequest(int id, int part) {
        this.id = id;
        this.part = part;
    }

    @NotNull
    public RequestType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getPart() {
        return part;
    }

    public static GetRequest parse(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int id = dis.readInt();
        int part = dis.readInt();
        return new GetRequest(id, part);
    }

    public void dump(@NotNull OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeInt(getId());
        dos.writeInt(getPart());
    }

}
