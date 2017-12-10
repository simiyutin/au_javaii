package requests;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SourcesResponse {
    private final List<HostPort> sources;

    public SourcesResponse(List<HostPort> sources) {
        this.sources = sources;
    }

    public int getSize() {
        return sources.size();
    }

    public List<HostPort> getSources() {
        return sources;
    }

    public static class HostPort {
        private byte[] ip;
        private int clientPort;

        public HostPort(byte[] ip, int clientPort) {
            this.ip = ip;
            this.clientPort = clientPort;
        }

        public byte[] getIp() {
            return ip;
        }

        public int getClientPort() {
            return clientPort;
        }
    }

    public static SourcesResponse parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int size = dis.readInt();
        List<HostPort> sources = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            byte ip[] = new byte[4];
            dis.read(ip, 0, 4);
            int port = dis.readInt();
            sources.add(new HostPort(ip, port));
        }
        return new SourcesResponse(sources);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getSize());
        for (HostPort hp : getSources()) {
            dos.write(hp.getIp(), 0, 4);
            dos.writeInt(hp.getClientPort());
        }
    }
}
