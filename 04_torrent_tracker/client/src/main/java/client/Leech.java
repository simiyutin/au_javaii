package client;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public class Leech {
    @NotNull
    private final Socket socket;

    public Leech(@NotNull Socket socket) {
        this.socket = socket;
    }

    @NotNull
    public Socket getSocket() {
        return socket;
    }
}
