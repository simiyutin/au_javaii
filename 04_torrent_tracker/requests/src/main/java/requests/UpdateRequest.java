package requests;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static requests.RequestType.UPDATE;

public class UpdateRequest {
    private final RequestType type = UPDATE;
    private final int clientPort;
    private final List<Integer> fileIds;

    public UpdateRequest(int clientPort, List<Integer> fileIds) {
        this.clientPort = clientPort;
        this.fileIds = fileIds;
    }

    public RequestType getType() {
        return type;
    }

    public int getClientPort() {
        return clientPort;
    }

    public int getCount() {
        return fileIds.size();
    }

    public List<Integer> getFileIds() {
        return fileIds;
    }

    public static UpdateRequest parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int clientPort = dis.readInt();
        int count = dis.readInt();
        List<Integer> fileIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int id = dis.readInt();
            fileIds.add(id);
        }
        return new UpdateRequest(clientPort, fileIds);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getType().getValue());
        dos.writeInt(getClientPort());
        dos.writeInt(getCount());
        for (Integer fileId : getFileIds()) {
            dos.writeInt(fileId);
        }
    }
}
