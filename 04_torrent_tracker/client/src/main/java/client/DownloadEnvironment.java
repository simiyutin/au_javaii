package client;

import requests.FileInfo;
import requests.HostPort;

import java.util.Map;
import java.util.Set;

public class DownloadEnvironment {
    private Map<Integer, Set<HostPort>> seeds;
    private final String indexPath;
    private final FileInfo fileInfo;

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public DownloadEnvironment(Map<Integer, Set<HostPort>> seeds, String indexPath, FileInfo fileInfo) {
        this.seeds = seeds;
        this.indexPath = indexPath;
        this.fileInfo = fileInfo;
    }

    public synchronized void updateSeeds(Map<Integer, Set<HostPort>> seeds) {
        this.seeds = seeds;
    }

    public synchronized Map<Integer, Set<HostPort>> getSeeds() {
        return seeds;
    }

    public String getIndexPath() {
        return indexPath;
    }
}
