package client;

import requests.ClientRequestCallback;
import requests.ClientRequestCallbackFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class PeerWorker implements Runnable {
    private final Leech leech;
    private final PeerEnvironment environment;

    public PeerWorker(Leech leech, PeerEnvironment environment) {
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
                System.out.println(String.format("%d peer worker wait for request", Thread.currentThread().getId()));
                ClientRequestCallback callback = ClientRequestCallbackFactory.parseRequest(leech.getSocket().getInputStream());
                System.out.println(String.format("%d peer worker got request", Thread.currentThread().getId()));
                callback.execute(leech, environment);
            } catch (EOFException | SocketException e) {

                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
