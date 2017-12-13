package requests;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByAddress(ip);
    }

    public int getPort() {
        return clientPort;
    }
}
