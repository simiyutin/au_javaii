package client;

import requests.FileInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PeerEnvironment {
    private final Set<FileInfo> seedingFiles;
    private final List<Leech> leeches = new ArrayList<>();
    private final IOService ioService;

    public PeerEnvironment(String indexPath) {
        this.ioService = new IOService(indexPath);
//        this.seedingFiles = new HashSet<>();
        this.seedingFiles = restoreSeedingFiles(indexPath);
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

    private Set<FileInfo> restoreSeedingFiles(String indexPath) {
        Set<FileInfo> result = new HashSet<>();
        File indexDir = new File(indexPath);
        if (!indexDir.exists()) {
            return result;
        }

        File[] files = indexDir.listFiles();
        if (files == null) {
            return result;
        }

        for (File meta : files) {
            if (meta.getName().startsWith("meta")) {
                int id = Integer.parseInt(meta.getName().substring(4));
                String fileName = "";
                long size = 0;

                try (DataInputStream dis = new DataInputStream(new FileInputStream(meta))) {
                    fileName = dis.readUTF();
                    size = dis.readLong();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileInfo fileInfo = new FileInfo(id, fileName, size);
                result.add(fileInfo);
            }
        }

        return result;
    }
}
