package requests;

import client.ClientEnvironment;
import client.IOService;
import client.Leech;

import java.io.IOException;
import java.util.List;

public class StatRequestCallback implements ClientRequestCallback {
    private final StatRequest request;

    public StatRequestCallback(StatRequest request) {
        this.request = request;
    }

    @Override
    public void execute(Leech leech, ClientEnvironment environment) throws IOException {
        int fileId = request.getFileId();
        List<Integer> fileParts = IOService.getAvailableFileParts(fileId);
        StatResponse response = new StatResponse(fileParts);
        response.dump(leech.getSocket().getOutputStream());
    }
}
