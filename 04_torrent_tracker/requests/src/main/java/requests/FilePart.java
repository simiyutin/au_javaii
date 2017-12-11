package requests;

import java.io.InputStream;

public class FilePart {
    private final int size;
    private final InputStream partStream;
    private final byte[] bytes;

    public FilePart(int size, InputStream partStream) {
        this.size = size;
        this.partStream = partStream;
        this.bytes = null;
    }

    public FilePart(byte[] bytes) {
        this.size = bytes.length;
        this.bytes = bytes;
        this.partStream = null;
    }

    public int getSize() {
        return size;
    }

    public InputStream getPartStream() {
        return partStream;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
