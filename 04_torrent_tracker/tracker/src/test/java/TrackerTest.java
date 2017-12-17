import org.junit.Test;
import requests.ListResponse;
import requests.RequestType;
import requests.UploadRequest;
import requests.UploadResponse;
import tracker.Tracker;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class TrackerTest {
    private final String basePath = "src/test/resources/";
    private final String trackerIndexPath = basePath + "/tracker_index/";

    private void prepareTests() {
        rmrf(trackerIndexPath);
    }

    @Test
    public void testSimple() throws IOException {
        prepareTests();

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
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

    public static void rmrf(String path) {
        rmrf(new File(path));
    }

    public static void rmrf(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                rmrf(f);
            }
        }
        file.delete();
    }
}
