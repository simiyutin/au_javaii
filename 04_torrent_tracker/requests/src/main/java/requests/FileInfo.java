package requests;

import java.io.*;
import java.util.*;

public class FileInfo {
    private final int id;
    private final String name;
    private final long size;

    public FileInfo(int id) {
        this.id = id;
        this.name = "FAKE";
        this.size = -1;
    }

    public FileInfo(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public int getFileId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return id == fileInfo.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getFileId());
        dos.writeUTF(getName());
        dos.writeLong(getSize());
    }

    public static FileInfo parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int id = dis.readInt();
        String name = dis.readUTF();
        long size = dis.readLong();
        return new FileInfo(id, name, size);
    }

    public static void dumpSet(Set<FileInfo> collection, OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(collection.size());
        for (FileInfo info : collection) {
            info.dump(os);
        }
    }

    public static Set<FileInfo> parseSet(InputStream is) throws IOException {
        Set<FileInfo> result = new HashSet<>();
        DataInputStream dis = new DataInputStream(is);
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            FileInfo info = parse(is);
            result.add(info);
        }
        return result;
    }
}
