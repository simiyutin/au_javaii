package client;

import requests.FilePart;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IOService {
    private static final String basePath = "resources/index/";
    public static final int PART_SIZE = 5 * 1000_000;

    // returns number of parts
    public static int split(File file, int id) throws IOException {
        long fileSize = file.length();
        int partSize = (int) (fileSize - 1) / PART_SIZE + 1;
        byte[] buffer = new byte[PART_SIZE];
        int bytesRead;
        FileInputStream fis = new FileInputStream(file);
        int currentPart = 0;
        while ((bytesRead = fis.read(buffer)) != -1) {
            File partFile = new File(String.format("%s/%d/%d", basePath, id, currentPart++));
            FileOutputStream fos = new FileOutputStream(partFile);
            fos.write(buffer, 0, bytesRead);
            fos.close();
        }

        return partSize;
    }

    public static List<Integer> getAvailableFileParts(int fileId) {
        File dir = new File(String.format("%s/%d", basePath, fileId));
        File[] files = dir.listFiles();
        List<Integer> result =
                Arrays.stream(files)
                        .map(f -> Integer.valueOf(f.getName()))
                        .collect(Collectors.toList());

        return result;
    }

    public static FilePart getPart(int fileId, int partId) throws FileNotFoundException {
        File partFile = new File(String.format("%s/%d/%d", basePath, fileId, partId));
        FilePart part = new FilePart(Math.toIntExact(partFile.length()), new FileInputStream(partFile));
        return part;
    }
}
