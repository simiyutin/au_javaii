package requests;

import client.PeerEnvironment;
import client.Leech;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GetRequestCallback implements ClientRequestCallback {
    @NotNull
    private final GetRequest request;

    public GetRequestCallback(@NotNull GetRequest request) {
        this.request = request;
    }


    @Override
    public void execute(@NotNull Leech leech, @NotNull PeerEnvironment environment) throws IOException {
        int fileId = request.getId();
        int partId = request.getPart();
        environment.getIoService().dumpFilePart(fileId, partId, leech.getSocket().getOutputStream());
    }
}
