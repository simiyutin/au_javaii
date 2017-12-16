package client;

import requests.GetRequest;
import requests.HostPort;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DownloadTask implements Runnable {
    private final int partId;
    private final DownloadEnvironment environment;

    public DownloadTask(DownloadEnvironment environment, int partId) {
        this.environment = environment;
        this.partId = partId;
    }

    @Override
    public void run() {
        while (true) {
            // todo synchronization
            // todo wait until new seeds
            if (!environment.getSeeds().containsKey(partId)) {
                continue;
            }
            if (environment.getSeeds().get(partId).size() == 0) {
                continue;
            }
            try {
                HostPort hostPort = environment.getSeeds().get(partId).iterator().next();
                Socket socket = new Socket(hostPort.getInetAddress(), hostPort.getPort());
                downloadPart(socket, environment.getFileInfo().getFileId(), partId);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadPart(Socket socket, int fileId, int partId) throws IOException {
        GetRequest request = new GetRequest(fileId, partId);
        request.dump(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        int size = dis.readInt();
        String filePath = String.format("%s/%d/%d", environment.getIndexPath(), fileId, partId);
        File partFile = new File(filePath);
        partFile.getParentFile().mkdirs();
        partFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(partFile);
        byte[] buffer = new byte[size];
        socket.getInputStream().read(buffer);
        fos.write(buffer);

        IOService.writeMeta(environment.getFileInfo(), environment.getIndexPath());
    }
}
