package client;

import requests.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

// сохраняет свое состояние двумя частями:
// список раздаваемых файлов лежит в файлах meta
// запросы на обрабатываемые кусочки обрабатываются с просмотром соответствующих директорий
public class Client {
    private Socket trackerSocket = null;
    private ServerSocket serverSocket = null;
    private PeerEnvironment environment = null;
    private final int TRACKER_PORT = 8081;
    private final int UPDATE_INTERVAL_MINUTES = 4;
    private final int PART_SIZE = 1024;


    public void start(int clientPort, String trackerHost, String indexPath) throws IOException {
        environment = new PeerEnvironment(indexPath);
        serverSocket = new ServerSocket(clientPort);
        trackerSocket = new Socket(trackerHost, TRACKER_PORT);
//        startDaemonThread(clientPort);
        startPeerServerThread();
    }

    // for testing
    void start(int clientPort, String indexPath) throws IOException {
        environment = new PeerEnvironment(indexPath);
        serverSocket = new ServerSocket(clientPort);
        startPeerServerThread();
    }

    // for testing
    PeerEnvironment getEnvironment() {
        return environment;
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (trackerSocket != null) {
                trackerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        updateTrackerSocket();
        UploadRequest request = new UploadRequest(file.getName(), file.length());
        request.dump(trackerSocket.getOutputStream());
        UploadResponse response = UploadResponse.parse(trackerSocket.getInputStream());
        environment.getIoService().scatter(file, PART_SIZE, response.getId());
        FileInfo fileInfo = new FileInfo(response.getId(), file.getName(), file.length());
        IOService.writeMeta(fileInfo, environment.getIoService().getIndexPath());
        environment.getSeedingFiles().add(fileInfo);
    }

    public void updateTracker() throws IOException {
        updateTrackerSocket();
        UpdateRequest updateRequest = new UpdateRequest(serverSocket.getLocalPort(), environment.getSeedingFileIds());
        updateRequest.dump(trackerSocket.getOutputStream());
        UpdateResponse updateResponse = UpdateResponse.parse(trackerSocket.getInputStream());
        if (!updateResponse.getStatus()) {
            System.out.println("bad update status!");
        }
    }

    public Set<FileInfo> listTracker() {
        try {
            updateTrackerSocket();
            ListRequest request = new ListRequest();
            request.dump(trackerSocket.getOutputStream());
            ListResponse response = ListResponse.parse(trackerSocket.getInputStream());
            return response.getFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<HostPort> executeSources(int fileId) throws IOException {
        updateTrackerSocket();
        SourcesRequest request = new SourcesRequest(fileId);
        request.dump(trackerSocket.getOutputStream());
        SourcesResponse response = SourcesResponse.parse(trackerSocket.getInputStream());
        return response.getSources();
    }

    public void downloadFile(FileInfo fileInfo, String targetDir) {
        try {

            final Map<Integer, Set<HostPort>> seeds = getSeeds(fileInfo);
            if (seeds.size() == 0) {
                return;
            }
            final DownloadEnvironment downloadEnvironment =
                    new DownloadEnvironment(seeds, environment.getIoService().getIndexPath(), fileInfo);

            Thread updater = new Thread(() -> {
                while (true) {
                    try {
                        TimeUnit.MINUTES.sleep(4);
                    } catch (InterruptedException e) {
                        return;
                    }
                    try {
                        downloadEnvironment.updateSeeds(getSeeds(fileInfo));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            updater.start();

            downloadFromSeeds(fileInfo, downloadEnvironment);

            updater.interrupt();
            environment.getIoService().gather(fileInfo.getFileId(), fileInfo.getName(), targetDir);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void downloadFromSeeds(FileInfo fileInfo, DownloadEnvironment downloadEnvironment) {
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

    private void startDaemonThread(int clientPort) {
        new Thread(() -> {
            try {
                updateTracker();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.MINUTES.sleep(UPDATE_INTERVAL_MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    private Map<Integer, Set<HostPort>> getSeeds(FileInfo fileInfo) throws IOException {
        updateTrackerSocket();

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

    private void updateTrackerSocket() throws IOException {
//        try {
//            if (trackerSocket.getInetAddress().isReachable(500)){
//                return;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //todo сделать нормально
        trackerSocket = new Socket(trackerSocket.getInetAddress(), TRACKER_PORT);
    }



}
