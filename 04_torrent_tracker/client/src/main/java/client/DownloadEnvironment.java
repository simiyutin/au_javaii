package client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import requests.*;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DownloadEnvironment {
    @NotNull
    private Map<Integer, Set<HostPort>> seeds;
    @Nullable
    private Thread updaterThread;
    @NotNull
    private final String indexPath;
    @NotNull
    private final FileInfo fileInfo;
    @NotNull
    private final TrackerHandle trackerHandle;

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public DownloadEnvironment(@NotNull TrackerHandle trackerHandle, @NotNull String indexPath, @NotNull FileInfo fileInfo) throws IOException {
        this.trackerHandle = trackerHandle;
        this.indexPath = indexPath;
        this.fileInfo = fileInfo;
        this.seeds = getSeedsFromTracker();
        this.updaterThread = null;
    }

    @NotNull
    public synchronized Map<Integer, Set<HostPort>> getSeeds() {
        return seeds;
    }

    @Nullable
    public synchronized Set<HostPort> getSeeds(int partId) {
        return seeds.get(partId);
    }

    @NotNull
    public String getIndexPath() {
        return indexPath;
    }

    public void startSeedUpdaterThread() {
        updaterThread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(4);
                } catch (InterruptedException e) {
                    return;
                }
                try {
                    Map<Integer, Set<HostPort>> newSeeds = getSeedsFromTracker();
                    synchronized (this) {
                        seeds = newSeeds;
                        this.notify();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        updaterThread.start();
    }

    public void interruptSeedUpdaterThread() {
        if (updaterThread != null) {
            updaterThread.interrupt();
        }
    }

    @NotNull
    private Map<Integer, Set<HostPort>> getSeedsFromTracker() throws IOException {
        Socket trackerSocket = trackerHandle.getNewSocket();
        SourcesRequest request = new SourcesRequest(fileInfo.getFileId());
        request.dump(trackerSocket.getOutputStream());
        SourcesResponse response = SourcesResponse.parse(trackerSocket.getInputStream());

        Map<Integer, Set<HostPort>> seeds = new HashMap<>();
        for (HostPort hostPort : response.getSources()) {
            try {
                Socket seedSocket = new Socket(hostPort.getInetAddress(), hostPort.getPort());
                StatRequest statRequest = new StatRequest(fileInfo.getFileId());
                statRequest.dump(seedSocket.getOutputStream());
                StatResponse seedResponse = StatResponse.parse(seedSocket.getInputStream());
                for (Integer part : seedResponse.getParts()) {
                    if (!seeds.containsKey(part)) {
                        seeds.put(part, new HashSet<>());
                    }
                    seeds.get(part).add(hostPort);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return seeds;
    }
}
