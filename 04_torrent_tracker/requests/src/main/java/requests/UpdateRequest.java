package requests;

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
}
