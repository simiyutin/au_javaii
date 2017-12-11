package client;

import requests.ClientRequestCallback;
import requests.ClientRequestCallbackFactory;

import java.io.IOException;

public class ClientWorker implements Runnable {
    private final Leech leech;
    private final ClientEnvironment environment;

    public ClientWorker(Leech leech, ClientEnvironment environment) {
        this.leech = leech;
        this.environment = environment;
    }

    @Override
    public void run() {
        while (true) {
            if (leech.getSocket().isClosed()) {
                break;
            }
            try {
                ClientRequestCallback callback = ClientRequestCallbackFactory.parseRequest(leech.getSocket().getInputStream());
                callback.execute(leech, environment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
