import client.Client;
import junitx.framework.Assert;
import junitx.framework.AssertionFailedError;
import org.junit.Test;
import requests.FileInfo;
import requests.HostPort;
import tracker.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.*;

public class MainTest {
    private final String basePath = "src/test/resources/";
    private final String indexPathFst = basePath + "/index_fst/";
    private final String indexPathSnd = basePath + "/index_snd/";
    private final String indexPathThd = basePath + "/index_thd/";
    private final String downloadsPath = basePath + "/downloads/";
    private final String trackerIndexPath = basePath + "/tracker_index/";

    private void clearFolders() {
        rmrf(indexPathFst);
        rmrf(indexPathSnd);
        rmrf(indexPathThd);
        rmrf(downloadsPath);
        rmrf(trackerIndexPath);
    }

    private void prepareTest() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clearFolders();
    }

    @Test
    public void testUpload() throws IOException {
        prepareTest();

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        Client client = new Client(11111, "localhost", indexPathFst);

        File file = new File(basePath + "test.txt");
        client.uploadFile(file.getPath());
        Set<FileInfo> listInfo = client.listTracker();

        assertTrue(listInfo.size() == 1);
        FileInfo expectedInfo = new FileInfo(0, file.getName(), file.length());

        assertEquals(expectedInfo, listInfo.iterator().next());
        tracker.stop();
        client.stop();
    }

    @Test
    public void testSources() throws IOException {
        prepareTest();

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        Client client = new Client(11111, "localhost", indexPathFst);

        File file = new File(basePath + "test.txt");
        client.uploadFile(file.getPath());

        client.updateTracker();

        List<HostPort> sources = client.executeSources(0);

        assertEquals(1, sources.size());
        byte[] ip = {127, 0, 0, 1};
        HostPort expected = new HostPort(ip, 11111);
        HostPort actual = sources.get(0);

        assertEquals(expected, actual);
        tracker.stop();
        client.stop();
    }

    @Test
    public void testUploadDownloadText() throws IOException {
        testUploadDownload("test.txt");
    }

    @Test
    public void testUploadDownloadBinary() throws IOException {
        testUploadDownload("kitty.jpg");
    }

    @Test
    public void testUploadDownloadEmpty() throws IOException {
        testUploadDownload("empty.txt");
    }

    private void testUploadDownload(String fileName) throws IOException {
        prepareTest();

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        Client client = new Client(11111, "localhost", indexPathFst);


        client.uploadFile(basePath + fileName);
        client.updateTracker();

        Client client2 = new Client(11112, "localhost", indexPathSnd);

        Set<FileInfo> infoList = client2.listTracker();
        assertTrue(infoList.size() == 1);
        FileInfo fileInfo = infoList.iterator().next();

        client2.downloadFile(fileInfo, downloadsPath);

        File expected = new File(basePath + fileName);
        File actual = new File(downloadsPath + fileName);

        assertBinaryEquals(expected, actual);
        tracker.stop();
        client.stop();
        client2.stop();
    }

    @Test
    public void testDownloadNotExists() throws IOException {
        prepareTest();

        String fileName = "notexists.txt";

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);

        Client client2 = new Client(11112, "localhost", indexPathSnd);

        Set<FileInfo> infoList = client2.listTracker();
        assertTrue(infoList.size() == 0);
        FileInfo fileInfo = new FileInfo(0, fileName, 100);

        client2.downloadFile(fileInfo, downloadsPath);

        File actual = new File(downloadsPath + fileName);
        assertFalse(actual.exists());

        tracker.stop();
        client2.stop();
    }

    @Test
    public void testUploadNotExists() throws IOException {
        prepareTest();

        String fileName = "notexists.txt";

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        Client client = new Client(11111, "localhost", indexPathFst);

        client.uploadFile(basePath + fileName);
        client.updateTracker();
        assertTrue(client.listTracker().size() == 0);

        tracker.stop();
        client.stop();
    }

    @Test
    public void testRestoringStateClient() throws IOException {
        prepareTest();
        String fileName = "kitty.jpg";

        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        Client client = new Client(11111, "localhost", indexPathFst);

        client.uploadFile(basePath + fileName);
        client.updateTracker();

        client.stop();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client = new Client(11113, "localhost", indexPathFst);
        client.updateTracker();

        Client client2 = new Client(11112, "localhost", indexPathSnd);

        Set<FileInfo> infoList = client2.listTracker();
        assertTrue(infoList.size() == 1);
        FileInfo fileInfo = infoList.iterator().next();

        client2.downloadFile(fileInfo, downloadsPath);

        File expected = new File(basePath + fileName);
        File actual = new File(downloadsPath + fileName);

        assertBinaryEquals(expected, actual);
        tracker.stop();
        client.stop();
        client2.stop();

    }

    @Test
    public void testRestoringStateServer() throws IOException {
        prepareTest();

        String fileName = "kitty.jpg";
        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        Client client = new Client(11111, "localhost", indexPathFst);

        client.uploadFile(basePath + fileName);

        tracker.stop();
        tracker = new Tracker();
        tracker.start(trackerIndexPath);

        client.updateTracker();

        Client client2 = new Client(11112, "localhost", indexPathSnd);

        Set<FileInfo> infoList = client2.listTracker();
        assertTrue(infoList.size() == 1);
        FileInfo fileInfo = infoList.iterator().next();

        client2.downloadFile(fileInfo, downloadsPath);

        File expected = new File(basePath + fileName);
        File actual = new File(downloadsPath + fileName);

        assertBinaryEquals(expected, actual);
        tracker.stop();
        client.stop();
        client2.stop();
    }

    @Test
    public void testDoubleUpdate() throws IOException {
        Tracker tracker = initTracker();
        Client client = initClient(11111, indexPathFst);

        client.updateTracker();
        client.updateTracker();

        tracker.stop();
        client.stop();
    }

    @Test
    // используется подгрузка состояний трекера и клиентов между перезапусками
    public void testMultipleSeeds() throws IOException {
        prepareTest();

        Tracker tracker = new Tracker();
        tracker.start(basePath + "/tracker_index_partial/");

        int fstPort = 11111;
        int sndPort = 11112;
        int thdPort = 11113;

        Client client1 = initClient(fstPort, basePath + "/index_fst_partial/");
        Client client2 = initClient(sndPort, basePath + "/index_snd_partial/");
        Client client3 = initClient(thdPort, indexPathThd);

        client1.updateTracker();
        client2.updateTracker();
        client3.updateTracker();

        String fileName = "kitty.jpg";
        File expected = new File(basePath + fileName);
        FileInfo fileInfo = new FileInfo(0, fileName, expected.length());
        client3.downloadFile(fileInfo, downloadsPath);

        File actual = new File(downloadsPath + fileName);
        assertBinaryEquals(expected, actual);
        tracker.stop();
        client1.stop();
        client2.stop();
        client3.stop();
    }

    public Tracker initTracker() throws IOException {
        Tracker tracker = new Tracker();
        tracker.start(trackerIndexPath);
        return tracker;
    }

    public Client initClient(int port, String indexPath) throws IOException {
        Client client = new Client(port, "localhost", indexPath);
        return client;
    }

    public static void assertBinaryEquals(File expected, File actual) {
        assertBinaryEquals((String)null, expected, actual);
    }

    public static void assertBinaryEquals(String message, File expected, File actual) {
        Assert.assertNotNull(message, expected);
        Assert.assertNotNull(message, actual);
        Assert.assertTrue("File does not exist [" + expected.getAbsolutePath() + "]", expected.exists());
        Assert.assertTrue("File does not exist [" + actual.getAbsolutePath() + "]", actual.exists());
        Assert.assertTrue("Expected file not readable", expected.canRead());
        Assert.assertTrue("Actual file not readable", actual.canRead());
        FileInputStream eis = null;
        FileInputStream ais = null;

        try {
            try {
                eis = new FileInputStream(expected);
                ais = new FileInputStream(actual);
                Assert.assertNotNull(message, expected);
                Assert.assertNotNull(message, actual);

                byte[] expBuff = new byte[8192];
                byte[] actBuff = new byte[8192];
                long pos = 0L;

                while (true) {

                    int expLength = eis.read(expBuff, 0, 8192);
                    int actLength = ais.read(actBuff, 0, 8192);
                    if (expLength < actLength) {
                        Assert.fail("actual file is longer");
                    }

                    if (expLength > actLength) {
                        Assert.fail("actual file is shorter");
                    }

                    if (expLength == -1) {
                        return;
                    }

                    for(int i = 0; i < expLength; ++i) {
                        if (expBuff[i] != actBuff[i]) {
                            String formatted = "";
                            if (message != null) {
                                formatted = message + " ";
                            }

                            Assert.fail(formatted + "files differ at byte " + (pos + (long)i + 1L));
                        }
                    }

                    pos += 8192;
                }

            } finally {
                eis.close();
                ais.close();
            }

        } catch (IOException var18) {
            throw new AssertionFailedError(var18);
        }
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
