package client;

import requests.FilePart;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IOService {
    private final String basePath;
    public IOService(String basePath) {
        this.basePath = basePath;
    }

    public int scatter(File file, int partSize, int fileId) throws IOException {
        byte[] buffer = new byte[partSize];
        int bytesRead;
        int numberOfParts = 0;
        File dir = new File(String.format("%s/index/%d", basePath, fileId));
        dir.mkdirs();
        try (FileInputStream fis = new FileInputStream(file)) {
            while ((bytesRead = fis.read(buffer)) != -1) {
                File partFile = new File(dir, String.valueOf(numberOfParts++));
                try(FileOutputStream fos = new FileOutputStream(partFile)) {
                    fos.write(buffer, 0, bytesRead);
                    fos.close();
                }
            }
        }
        return numberOfParts;
    }

    public void gather(int fileId, String name) throws IOException {
        File targetFile = new File(String.format("%s/downloads/%s", basePath, name));
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        String dirPath = String.format("%s/index/%d", basePath, fileId);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new FileNotFoundException(dirPath);
        }
        File[] parts = dir.listFiles();
        if (parts == null) {
            throw new IOException("error while reading " + dirPath);
        }
        Arrays.sort(parts, Comparator.comparing(f -> Integer.valueOf(f.getName())));
        try(FileOutputStream targetStream = new FileOutputStream(targetFile)) {
            for (File part : parts) {
                try(FileInputStream partStream = new FileInputStream(part)) {
                    move(partStream, targetStream);
                }
            }
        }
    }

    public List<Integer> getAvailableFileParts(int fileId) {
        File dir = new File(String.format("%s/index/%d", basePath, fileId));
        File[] files = dir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .map(f -> Integer.valueOf(f.getName()))
                .collect(Collectors.toList());
    }

    public FilePart getPart(int fileId, int partId) throws FileNotFoundException {
        File partFile = new File(String.format("%s/index/%d/%d", basePath, fileId, partId));
        return new FilePart(Math.toIntExact(partFile.length()), new FileInputStream(partFile));
    }

    public static void move(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }
}
