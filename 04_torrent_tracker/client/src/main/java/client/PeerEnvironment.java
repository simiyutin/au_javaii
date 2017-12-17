package client;

import org.jetbrains.annotations.NotNull;
import requests.FileInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PeerEnvironment {
    @NotNull
    private final Set<FileInfo> seedingFiles;
    @NotNull
    private final List<Leech> leeches;
    @NotNull
    private final IOService ioService;

    public PeerEnvironment(@NotNull String indexPath) {
        this.ioService = new IOService(indexPath);
        this.seedingFiles = restoreSeedingFiles(indexPath);
        this.leeches = new ArrayList<>();
    }

    @NotNull
    public Set<FileInfo> getSeedingFiles() {
        return seedingFiles;
    }

    @NotNull
    public List<Integer> getSeedingFileIds() {
        return seedingFiles.stream().map(FileInfo::getFileId).collect(Collectors.toList());
    }

    @NotNull
    public List<Leech> getLeeches() {
        return leeches;
    }

    @NotNull
    public IOService getIoService() {
        return ioService;
    }

    @NotNull
    private Set<FileInfo> restoreSeedingFiles(@NotNull String indexPath) {
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
