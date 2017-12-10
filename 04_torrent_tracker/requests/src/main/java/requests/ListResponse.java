package requests;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListResponse {
    private final List<FileInfo> files;

    public ListResponse(List<FileInfo> files) {
        this.files = files;
    }

    public int getCount() {
        return files.size();
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public static ListResponse parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int count = dis.readInt();
        List<FileInfo> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int id = dis.readInt();
            String name = dis.readUTF();
            long size = dis.readLong();
            FileInfo info = new FileInfo(id, name, size);
            files.add(info);
        }
        return new ListResponse(files);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getFiles().size());
        for (FileInfo info : getFiles()) {
            dos.writeInt(info.getId());
            dos.writeUTF(info.getName());
            dos.writeLong(info.getSize());
        }
    }
}
