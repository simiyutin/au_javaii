package client;

public class DownloadTask implements Runnable {
    private final String fileName;

    public DownloadTask(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }
}
