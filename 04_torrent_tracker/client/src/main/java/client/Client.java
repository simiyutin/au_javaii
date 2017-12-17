package client;

import requests.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {
    private final TrackerHandle trackerHandle;
    private final ServerSocket serverSocket;
    private final PeerEnvironment environment;
    private final int TRACKER_PORT = 8081;
    private final int UPDATE_INTERVAL_MINUTES = 4;
    private final int PART_SIZE = 1024;

    public Client(int clientPort, String trackerHost, String indexPath) throws IOException {
        this.trackerHandle = new TrackerHandle(trackerHost, TRACKER_PORT);
        this.serverSocket = new ServerSocket(clientPort);
        this.environment = new PeerEnvironment(indexPath);
        startDaemonThread();
        startPeerServerThread();
    }

    Client(int clientPort, String indexPath) throws IOException {
        this.trackerHandle = new TrackerHandle();
        this.serverSocket = new ServerSocket(clientPort);
        this.environment = new PeerEnvironment(indexPath);
        startPeerServerThread();
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        Socket trackerSocket = trackerHandle.getNewSocket();
        UploadRequest request = new UploadRequest(file.getName(), file.length());
        request.dump(trackerSocket.getOutputStream());
        UploadResponse response = UploadResponse.parse(trackerSocket.getInputStream());
        environment.getIoService().scatter(file, PART_SIZE, response.getId());
        FileInfo fileInfo = new FileInfo(response.getId(), file.getName(), file.length());
        IOService.writeMeta(fileInfo, environment.getIoService().getIndexPath());
        environment.getSeedingFiles().add(fileInfo);
    }

    public void updateTracker() throws IOException {
        Socket trackerSocket = trackerHandle.getNewSocket();
        UpdateRequest updateRequest = new UpdateRequest(serverSocket.getLocalPort(), environment.getSeedingFileIds());
        updateRequest.dump(trackerSocket.getOutputStream());
        UpdateResponse updateResponse = UpdateResponse.parse(trackerSocket.getInputStream());
        if (!updateResponse.getStatus()) {
            System.out.println("bad update status!");
        }
    }

    public Set<FileInfo> listTracker() throws IOException {
        Socket trackerSocket = trackerHandle.getNewSocket();
        ListRequest request = new ListRequest();
        request.dump(trackerSocket.getOutputStream());
        ListResponse response = ListResponse.parse(trackerSocket.getInputStream());
        return response.getFiles();
    }

    public List<HostPort> executeSources(int fileId) throws IOException {
        Socket trackerSocket = trackerHandle.getNewSocket();
        SourcesRequest request = new SourcesRequest(fileId);
        request.dump(trackerSocket.getOutputStream());
        SourcesResponse response = SourcesResponse.parse(trackerSocket.getInputStream());
        return response.getSources();
    }

    public void downloadFile(FileInfo fileInfo, String targetDir) throws IOException {
        final DownloadEnvironment downloadEnvironment =
                new DownloadEnvironment(trackerHandle, environment.getIoService().getIndexPath(), fileInfo);

        if (downloadEnvironment.getSeeds().size() == 0) {
            return;
        }

        downloadEnvironment.startSeedUpdaterThread();
        downloadFileParts(fileInfo, downloadEnvironment);
        downloadEnvironment.interruptSeedUpdaterThread();

        environment.getIoService().gather(fileInfo.getFileId(), fileInfo.getName(), targetDir);
    }

    void downloadFileParts(FileInfo fileInfo, DownloadEnvironment downloadEnvironment) {
        final int numberOfParts = IOService.getNumberOfParts(fileInfo.getSize(), PART_SIZE);
        final List<Thread> downloaders = new ArrayList<>();
        for (int i = 0; i < numberOfParts; i++) {
            downloaders.add(new Thread(new DownloadTask(downloadEnvironment, i)));
            downloaders.get(i).start();
        }

        for (Thread t : downloaders) {
            try {
                t.join();
                environment.getSeedingFiles().add(fileInfo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startDaemonThread() {
        new Thread(() -> {
            while (true) {
                try {
                    updateTracker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.MINUTES.sleep(UPDATE_INTERVAL_MINUTES);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void startPeerServerThread() {
        new Thread(() -> {
            try {
                while (true) {
                    System.out.println(String.format("%d peer server waiting for leach", Thread.currentThread().getId()));
                    Socket socket = serverSocket.accept();
                    System.out.println(String.format("%d peer server got leach", Thread.currentThread().getId()));
                    Leech leech = new Leech(socket);
                    synchronized (environment) {
                        environment.getLeeches().add(leech);
                    }
                    new Thread(new PeerWorker(leech, environment)).start();
                }
            }
            catch (IOException e) {
                System.out.println("peer server ended");
            }
        }).start();
    }
}
