package requests;

import org.jetbrains.annotations.NotNull;

import java.io.*;

import static requests.RequestType.LIST;

public class ListRequest {
    private final RequestType type = LIST;

    public RequestType getType() {
        return type;
    }

    @NotNull
    public static ListRequest parse(@NotNull InputStream is) throws IOException {
        return new ListRequest();
    }

    public void dump(@NotNull OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
    }
}
