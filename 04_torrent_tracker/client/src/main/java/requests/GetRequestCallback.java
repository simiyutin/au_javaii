package requests;

import client.ClientEnvironment;
import client.IOService;
import client.Leech;

import java.io.IOException;

public class GetRequestCallback implements ClientRequestCallback {
    private final GetRequest request;

    public GetRequestCallback(GetRequest request) {
        this.request = request;
    }


    @Override
    public void execute(Leech leech, ClientEnvironment environment) throws IOException {
        int fileId = request.getId();
        int partId = request.getPart();
        environment.getIoService().dumpFilePart(fileId, partId, leech.getSocket().getOutputStream());
    }
}
