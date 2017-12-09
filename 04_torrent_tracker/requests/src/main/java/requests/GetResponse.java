package requests;

public class GetResponse {
    private final byte[] bytes;

    public GetResponse(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getSize() {
        return bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
