package requests;

import static requests.RequestType.STAT;

public class StatRequest {
    private final RequestType type = STAT;
    private final int fileId;

    public StatRequest(int fileId) {
        this.fileId = fileId;
    }

    public RequestType getType() {
        return type;
    }

    public int getFileId() {
        return fileId;
    }
}
