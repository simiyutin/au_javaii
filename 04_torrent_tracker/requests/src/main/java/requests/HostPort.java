package requests;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

public class HostPort {
    private byte[] ip;
    private int clientPort;

    public HostPort(byte[] ip, int clientPort) {
        this.ip = ip;
        this.clientPort = clientPort;
    }

    public byte[] getIp() {
        return ip;
    }

    @NotNull
    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByAddress(ip);
    }

    public int getPort() {
        return clientPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostPort hostPort = (HostPort) o;
        return clientPort == hostPort.clientPort &&
                Arrays.equals(ip, hostPort.ip);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(clientPort);
        result = 31 * result + Arrays.hashCode(ip);
        return result;
    }
}
