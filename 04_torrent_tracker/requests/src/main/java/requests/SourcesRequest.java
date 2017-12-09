package requests;

import static requests.RequestType.SOURCES;

public class SourcesRequest {
    private final RequestType type = SOURCES;
    private final int fileId;

    public SourcesRequest(int fileId) {
        this.fileId = fileId;
    }

    public int getFileId() {
        return fileId;
    }
}
