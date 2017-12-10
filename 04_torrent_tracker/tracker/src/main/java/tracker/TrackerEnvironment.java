package tracker;

import requests.FileInfo;

import java.util.*;

public class TrackerEnvironment {
    private final List<Peer> peers = new ArrayList<>();
    private final Map<FileInfo, Set<Peer>> index = new HashMap<>();

    public List<Peer> getPeers() {
        return peers;
    }

    public Set<Peer> getPeers(int fileId) {
        FileInfo info = new FileInfo(fileId);
        return index.get(info);
    }

    public Map<FileInfo, Set<Peer>> getIndex() {
        return index;
    }

    public int addFile(Peer peer, String name, long size) {
        int id = index.keySet().size();
        FileInfo info = new FileInfo(id, name, size);
        Set<Peer> peers = new HashSet<>();
        peers.add(peer);
        index.put(info, peers);
        return id;
    }

}
