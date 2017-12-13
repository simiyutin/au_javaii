package client;

import requests.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

// отправляет и делает запросы
// нужен тред, который будет обслуживать входящие соединения
// и тред, который будет делать запросы к серверу каждые 5 минут
// и тред, который будет отвечать на запросы пользователя. Видимо, это просто главный тред программы
public class Client {
    private ServerSocket serverSocket = null;
    private final int TRACKER_PORT = 8081;
    private final int UPDATE_INTERVAL = 4;
    private Socket trackerSocket = null;
    private final ClientEnvironment environment = new ClientEnvironment();
    private final int FILE_SIZE = 5 * 1000_000;


    public void start(int clientPort, String trackerHost) throws IOException {
        serverSocket = new ServerSocket(clientPort);
        trackerSocket = new Socket(trackerHost, TRACKER_PORT);
        startDaemonThread(clientPort);
        startPeerServerThread();
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
                TimeUnit.MINUTES.sleep(UPDATE_INTERVAL);
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

    public void stop() {

    }

    public void uploadFile(String path) throws IOException {
        File file = new File(path);
        UploadRequest request = new UploadRequest(file.getName(), file.length());
        request.dump(trackerSocket.getOutputStream());
        UploadResponse response = UploadResponse.parse(trackerSocket.getInputStream());
        environment.getIoService().scatter(file, FILE_SIZE, response.getId());
        FileInfo info = new FileInfo(response.getId(), file.getName(), file.length());
        environment.getSeedingFiles().add(info);
    }

    public void downloadFile(String fileName) {
        new Thread(new DownloadTask(fileName)).start();
    }

}
