package tracker;

import requests.FileInfo;

import java.util.*;

public class TrackerEnvironment {
    private final Set<Peer> peers = new HashSet<>();
    private final Map<FileInfo, Set<Peer>> index = new HashMap<>();

    public Set<Peer> getPeers() {
        return peers;
    }

    public void updatePeerForFile(Peer peer, int fileId) {
        FileInfo fakeInfo = new FileInfo(fileId);
        Set<Peer> filePeers = index.get(fakeInfo);
        if (filePeers == null) {
            return;
        }

        filePeers.remove(peer);
        filePeers.add(peer);
    }

    public void updatePeer(Peer peer) {
        peers.remove(peer);
        peers.add(peer);
    }

    public Set<Peer> getPeers(int fileId) {
        FileInfo info = new FileInfo(fileId);
        Set<Peer> peers = index.get(info);
        return peers == null ? new HashSet<>() : peers;
    }

    public Map<FileInfo, Set<Peer>> getIndex() {
        return index;
    }

    public int addFile(String name, long size) {
        int id = index.keySet().size();
        FileInfo info = new FileInfo(id, name, size);
        index.put(info, new HashSet<>());
        return id;
    }

}
