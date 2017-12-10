package requests;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StatResponse {
    private final List<Integer> parts;

    public StatResponse(List<Integer> parts) {
        this.parts = parts;
    }

    public int getCount() {
        return parts.size();
    }

    public List<Integer> getParts() {
        return parts;
    }

    public static StatResponse parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int count = dis.readInt();
        List<Integer> parts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int part = dis.readInt();
            parts.add(part);
        }
        return new StatResponse(parts);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getCount());
        for (Integer part : getParts()) {
            dos.writeInt(part);
        }
    }
}
