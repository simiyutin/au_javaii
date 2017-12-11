package requests;

import client.ClientEnvironment;
import client.Leech;

import java.io.IOException;

public interface ClientRequestCallback {
    void execute(Leech peer, ClientEnvironment environment) throws IOException;
}
