package requests;

import java.io.*;

import static requests.RequestType.LIST;

public class ListRequest {
    private final RequestType type = LIST;

    public RequestType getType() {
        return type;
    }

    public static ListRequest parse(InputStream is) throws IOException {
        return new ListRequest();
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
    }
}
