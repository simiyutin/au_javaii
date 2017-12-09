package requests;

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
        private String ip;
        private int clientPort;

        public HostPort(String ip, int clientPort) {
            this.ip = ip;
            this.clientPort = clientPort;
        }

        public String getIp() {
            return ip;
        }

        public int getClientPort() {
            return clientPort;
        }
    }
}
