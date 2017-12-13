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
        int id = request.getId();
        int part = request.getPart();
        FilePart filePart = environment.getIoService().getPart(id, part);
        GetResponse response = new GetResponse(filePart);
        response.dump(leech.getSocket().getOutputStream());
        filePart.getPartStream().close();
    }
}
