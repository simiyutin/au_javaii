package requests;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StatResponse {
    @NotNull
    private final List<Integer> parts;

    public StatResponse(@NotNull List<Integer> parts) {
        this.parts = parts;
    }

    public int getCount() {
        return parts.size();
    }

    @NotNull
    public List<Integer> getParts() {
        return parts;
    }

    @NotNull
    public static StatResponse parse(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int count = dis.readInt();
        List<Integer> parts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int part = dis.readInt();
            parts.add(part);
        }
        return new StatResponse(parts);
    }

    @NotNull
    public void dump(@NotNull OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getCount());
        for (Integer part : getParts()) {
            dos.writeInt(part);
        }
    }
}
