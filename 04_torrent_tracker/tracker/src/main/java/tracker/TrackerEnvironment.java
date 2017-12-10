package tracker;

import java.util.*;

public class TrackerEnvironment {
    private final List<Peer> peers = new ArrayList<>();
    private final Map<FileInfo, Set<Peer>> index = new HashMap<>();

    public List<Peer> getPeers() {
        return peers;
    }

    public Map<FileInfo, Set<Peer>> getIndex() {
        return index;
    }

    public static class FileInfo {
        private final int id;
        private final String name;
        private final long size;

        public FileInfo(int id, String name, long size) {
            this.id = id;
            this.name = name;
            this.size = size;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }
    }
}
