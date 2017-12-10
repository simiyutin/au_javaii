import org.junit.Test;
import requests.ListResponse;
import requests.RequestType;
import requests.UploadRequest;
import requests.UploadResponse;
import tracker.Tracker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SimpleTest {
    @Test
    public void testSimple() throws IOException {
        Tracker tracker = new Tracker();
        tracker.start();
        Socket socket = new Socket("localhost", 8081);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        UploadRequest uploadRequest = new UploadRequest("abc.txt", 100);
        uploadRequest.dump(socket.getOutputStream());
        UploadResponse uploadResponse = UploadResponse.parse(socket.getInputStream());

        dos.writeInt(RequestType.LIST.getValue());
        ListResponse response = ListResponse.parse(socket.getInputStream());
        System.out.println(response.getFiles());
    }
}
