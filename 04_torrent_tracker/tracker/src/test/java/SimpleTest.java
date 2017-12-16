import org.junit.Test;
import requests.ListResponse;
import requests.RequestType;
import requests.UploadRequest;
import requests.UploadResponse;
import tracker.Tracker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

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
        assertTrue(uploadResponse.getId() == 0);

        UploadRequest uploadRequest2 = new UploadRequest("abc.txt", 100);
        uploadRequest.dump(socket.getOutputStream());
        UploadResponse uploadResponse2 = UploadResponse.parse(socket.getInputStream());
        assertTrue(uploadResponse2.getId() == 1);

        dos.writeInt(RequestType.LIST.getValue());
        ListResponse response = ListResponse.parse(socket.getInputStream());
        System.out.println(response.getFiles());
    }
}
