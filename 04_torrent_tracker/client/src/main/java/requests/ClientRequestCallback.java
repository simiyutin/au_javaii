package requests;

import client.PeerEnvironment;
import client.Leech;

import java.io.IOException;

public interface ClientRequestCallback {
    void execute(Leech peer, PeerEnvironment environment) throws IOException;
}
