package requests;

import client.PeerEnvironment;
import client.Leech;

import java.io.IOException;

public class GetRequestCallback implements ClientRequestCallback {
    private final GetRequest request;

    public GetRequestCallback(GetRequest request) {
        this.request = request;
    }


    @Override
    public void execute(Leech leech, PeerEnvironment environment) throws IOException {
        int fileId = request.getId();
        int partId = request.getPart();
        environment.getIoService().dumpFilePart(fileId, partId, leech.getSocket().getOutputStream());
    }
}
