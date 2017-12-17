package tracker;

import org.jetbrains.annotations.NotNull;
import requests.FileInfo;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrackerEnvironment {
    @NotNull
    private final Set<Peer> peers = ConcurrentHashMap.newKeySet();
    @NotNull
    private final Map<FileInfo, Set<Peer>> index;
    @NotNull
    private final String trackerIndexPath;

    public TrackerEnvironment(@NotNull String trackerIndexPath) {
        new File(trackerIndexPath).mkdirs();
        this.trackerIndexPath = trackerIndexPath;
        this.index = loadCatalog();
    }

    @NotNull
    public String getTrackerIndexPath() {
        return trackerIndexPath;
    }

    @NotNull
    public Set<Peer> getPeers() {
        return peers;
    }

    public synchronized void updatePeerForFile(Peer peer, int fileId) {
        FileInfo fakeInfo = new FileInfo(fileId);
        Set<Peer> filePeers = index.get(fakeInfo);
        if (filePeers == null) {
            System.out.println("peer tried to update unknown file!");
            return;
        }

        filePeers.remove(peer);
        filePeers.add(peer);
    }

    public void updatePeer(@NotNull Peer peer) {
        peers.remove(peer);
        peers.add(peer);
    }

    @NotNull
    public Set<Peer> getPeers(int fileId) {
        FileInfo info = new FileInfo(fileId);
        Set<Peer> peers = index.get(info);
        return peers == null ? new HashSet<>() : peers;
    }

    @NotNull
    public Map<FileInfo, Set<Peer>> getIndex() {
        return index;
    }

    public synchronized int addFile(@NotNull String name, long size) {
        int id = index.keySet().size();
        FileInfo info = new FileInfo(id, name, size);
        dumpInfo(info);
        index.put(info, new HashSet<>());
        return id;
    }

    private void dumpInfo(@NotNull FileInfo info) {
        try {
            FileOutputStream fos = new FileOutputStream(String.format("%s/%d", getTrackerIndexPath(), info.getFileId()));
            info.dump(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private Map<FileInfo, Set<Peer>> loadCatalog() {
        Set<FileInfo> infos = loadInfos();
        Map<FileInfo, Set<Peer>> result = new ConcurrentHashMap<>();

        infos.forEach(info -> result.put(info, new HashSet<>()));

        return result;
    }

    @NotNull
    private Set<FileInfo> loadInfos() {
        Set<FileInfo> result = new HashSet<>();

        File dir = new File(getTrackerIndexPath());
        if (!dir.exists()) {
            return result;
        }

        File[] files = dir.listFiles();

        if (files == null) {
            return result;
        }

        for (File file : files) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileInfo info = FileInfo.parse(fis);
                result.add(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
