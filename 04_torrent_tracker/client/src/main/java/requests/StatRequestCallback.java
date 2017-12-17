package requests;

import client.PeerEnvironment;
import client.Leech;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class StatRequestCallback implements ClientRequestCallback {
    @NotNull
    private final StatRequest request;

    public StatRequestCallback(@NotNull StatRequest request) {
        this.request = request;
    }

    @Override
    public void execute(@NotNull Leech leech, @NotNull PeerEnvironment environment) throws IOException {
        int fileId = request.getFileId();
        List<Integer> fileParts = environment.getIoService().getAvailableFileParts(fileId);
        StatResponse response = new StatResponse(fileParts);
        response.dump(leech.getSocket().getOutputStream());
    }
}
