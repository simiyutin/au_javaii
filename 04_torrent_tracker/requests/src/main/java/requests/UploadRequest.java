package requests;

import static requests.RequestType.UPLOAD;

public class UploadRequest {
    private final RequestType type = UPLOAD;
    private final String name;
    private final int size;

    public UploadRequest(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public RequestType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
