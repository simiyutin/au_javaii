package client;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class IOService {
    private final String basePath;
    public IOService(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public int scatter(File file, int partSize, int fileId) throws IOException {
        byte[] buffer = new byte[partSize];
        int bytesRead;
        int numberOfParts = 0;
        File dir = new File(String.format("%s/%d", basePath, fileId));
        dir.mkdirs();
        if (file.exists() && file.length() == 0) {
            new File(dir, String.valueOf(0)).createNewFile();
            return 0;
        }
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

    public void gather(int fileId, String name, String targetDir) throws IOException {
        String partsDir = String.format("%s/%d", basePath, fileId);
        File dir = new File(partsDir);
        if (!dir.exists()) {
            throw new FileNotFoundException(partsDir);
        }
        File[] parts = dir.listFiles();
        if (parts == null) {
            throw new RuntimeException("error occurred while reading path");
        }

        File targetFile = new File(String.format("%s/%s", targetDir, name));
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        Arrays.sort(parts, Comparator.comparing(f -> Integer.valueOf(f.getName())));
        try(FileOutputStream targetStream = new FileOutputStream(targetFile)) {
            for (File part : parts) {
                try(FileInputStream partStream = new FileInputStream(part)) {
                    move(partStream, targetStream);
                }
            }
        }
    }

    public static int getNumberOfParts(long fileSize, int partSize) {
        return (int) (fileSize - 1) / partSize + 1;
    }

    public List<Integer> getAvailableFileParts(int fileId) {
        File dir = new File(String.format("%s/%d", basePath, fileId));
        File[] files = dir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .map(f -> Integer.valueOf(f.getName()))
                .collect(Collectors.toList());
    }

    public void dumpFilePart(int fileId, int partId, OutputStream os) throws IOException {
        File partFile = new File(String.format("%s/%d/%d", basePath, fileId, partId));
        int size = Math.toIntExact(partFile.length());
        try (
                FileInputStream fis = new FileInputStream(partFile);
                DataOutputStream dos = new DataOutputStream(os)
        ) {
            dos.writeInt(size);
            byte[] buffer = new byte[size];
            fis.read(buffer);
            os.write(buffer);
        }
    }

    public static void move(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }
}
