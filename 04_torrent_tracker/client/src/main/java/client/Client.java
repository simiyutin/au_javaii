package client;

import requests.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// отправляет и делает запросы
// нужен тред, который будет обслуживать входящие соединения
// и тред, который будет делать запросы к серверу каждые 5 минут
// и тред, который будет отвечать на запросы пользователя. Видимо, это просто главный тред программы
public class Client {
    private Socket trackerSocket = null;
    private ServerSocket serverSocket = null;
    private final ClientEnvironment environment = new ClientEnvironment();
    private final int TRACKER_PORT = 8081;
    private final int UPDATE_INTERVAL_MINUTES = 4;
    private final int PART_SIZE = 1024;


    public void start(int clientPort, String trackerHost) throws IOException {
        serverSocket = new ServerSocket(clientPort);
        trackerSocket = new Socket(trackerHost, TRACKER_PORT);
        startDaemonThread(clientPort);
        startPeerServerThread();
    }

    public void stop() {
        throw new UnsupportedOperationException();
    }

    public void uploadFile(String path) throws IOException {
        File file = new File(path);
        UploadRequest request = new UploadRequest(file.getName(), file.length());
        request.dump(trackerSocket.getOutputStream());
        UploadResponse response = UploadResponse.parse(trackerSocket.getInputStream());
        environment.getIoService().scatter(file, PART_SIZE, response.getId());
        FileInfo info = new FileInfo(response.getId(), file.getName(), file.length());
        environment.getSeedingFiles().add(info);
    }

    public Set<FileInfo> listTracker() {
        try {
            ListRequest request = new ListRequest();
            request.dump(trackerSocket.getOutputStream());
            ListResponse response = ListResponse.parse(trackerSocket.getInputStream());
            return response.getFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadFile(FileInfo fileInfo) {
        try {

            SourcesRequest request = new SourcesRequest(fileInfo.getFileId());
            request.dump(trackerSocket.getOutputStream());
            SourcesResponse response = SourcesResponse.parse(trackerSocket.getInputStream());

            Set<Integer> partsLeft = ConcurrentHashMap.newKeySet();
            int numberOfParts = IOService.getNumberOfParts(fileInfo.getSize(), PART_SIZE);
            for (int i = 0; i < numberOfParts; i++) {
                partsLeft.add(i);
            }

            while (partsLeft.size() > 0) {
                Map<Integer, Set<HostPort>> seeds = getSeeds(partsLeft, response.getSources(), fileInfo.getFileId());
                seeds.forEach((partId, peers) -> {
                    while (peers.size() > 0) {
                        Iterator<HostPort> it = peers.iterator();
                        try {
                            HostPort hostPort = it.next();
                            Socket socket = new Socket(hostPort.getInetAddress(), hostPort.getPort());
                            downloadPart(socket, fileInfo.getFileId(), partId);
                            partsLeft.remove(partId);
                            environment.getSeedingFiles().add(fileInfo);
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                            it.remove();
                        }
                    }
                });
            }

            environment.getIoService().gather(fileInfo.getFileId(), fileInfo.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDaemonThread(int clientPort) {
        new Thread(() -> {
            UpdateRequest request;
            synchronized (environment) {
                request = new UpdateRequest(clientPort, environment.getSeedingFileIds());
            }
            try {
                request.dump(trackerSocket.getOutputStream());
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
                    System.out.println("client: waiting for leech");
                    Socket socket = serverSocket.accept();
                    Leech leech = new Leech(socket);
                    synchronized (environment) {
                        environment.getLeeches().add(leech);
                    }
                    new Thread(new ClientWorker(leech, environment)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Map<Integer, Set<HostPort>> getSeeds(Set<Integer> partsLeft, List<HostPort> sources, int fileId) {
        Map<Integer, Set<HostPort>> seeds = new HashMap<>();
        for (Integer pendingPart : partsLeft) {
            seeds.put(pendingPart, new HashSet<>());
        }
        for (HostPort hostPort : sources) {
            try {
                Socket seedSocket = new Socket(hostPort.getInetAddress(), hostPort.getPort());
                StatRequest statRequest = new StatRequest(fileId);
                statRequest.dump(seedSocket.getOutputStream());
                StatResponse seedResponse = StatResponse.parse(seedSocket.getInputStream());
                for (Integer part : seedResponse.getParts()) {
                    seeds.get(part).add(hostPort);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return seeds;
    }

    private void downloadPart(Socket socket, int fileId, int partId) throws IOException {
        GetRequest request = new GetRequest(fileId, partId);
        request.dump(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        //todo протестить, останавливается ли move без спецификации size и убрать size вообще, если останавливается
        int size = dis.readInt();
        String filePath = String.format("%s/index/%d/%d", environment.getIoService().getBasePath(), fileId, partId);
        File partFile = new File(filePath);
        partFile.getParentFile().mkdirs();
        partFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(partFile);
        IOService.move(socket.getInputStream(), fos);
    }

}
