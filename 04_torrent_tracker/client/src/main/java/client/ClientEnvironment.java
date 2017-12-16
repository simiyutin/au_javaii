package client;

import requests.FileInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientEnvironment {
    private final Set<FileInfo> seedingFiles = new HashSet<>();
    private final List<Leech> leeches = new ArrayList<>();
    private final IOService ioService;

    public ClientEnvironment(String indexPath) {
        this.ioService = new IOService(indexPath);
    }

    public Set<FileInfo> getSeedingFiles() {
        return seedingFiles;
    }

    public List<Integer> getSeedingFileIds() {
        return seedingFiles.stream().map(FileInfo::getFileId).collect(Collectors.toList());
    }

    public List<Leech> getLeeches() {
        return leeches;
    }

    public IOService getIoService() {
        return ioService;
    }
}
