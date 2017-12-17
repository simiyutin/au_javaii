package client;

import org.jetbrains.annotations.NotNull;
import requests.GetRequest;
import requests.HostPort;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

public class DownloadTask implements Runnable {
    private final int partId;
    @NotNull
    private final DownloadEnvironment environment;

    public DownloadTask(@NotNull DownloadEnvironment environment, int partId) {
        this.environment = environment;
        this.partId = partId;
    }

    @Override
    public void run() {
        while (true) {
            Set<HostPort> seeds = environment.getSeeds(partId);
            if (seeds != null) {
                Iterator<HostPort> it = seeds.iterator();
                while (it.hasNext()) {
                    try {
                        HostPort hostPort = it.next();
                        Socket socket = new Socket(hostPort.getInetAddress(), hostPort.getPort());
                        downloadPart(socket, environment.getFileInfo().getFileId(), partId);
                        return;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    it.remove();
                }
            }
            synchronized (environment) {
                try {
                    environment.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private void downloadPart(@NotNull Socket socket, int fileId, int partId) throws IOException {
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
