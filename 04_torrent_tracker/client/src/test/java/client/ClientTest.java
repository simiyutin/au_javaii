package client;

import junitx.framework.Assert;
import junitx.framework.AssertionFailedError;
import org.junit.Test;
import requests.FileInfo;
import requests.HostPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class ClientTest {

    private final String basePath = "src/test/resources/";
    private final String indexPathFst = basePath + "/index_fst/";
    private final String indexPathSnd = basePath + "/index_snd/";
    private final String indexPathThd = basePath + "/index_thd/";
    private final String downloadsPath = basePath + "/downloads/";

    private void clearFolders() {
        rmrf(indexPathFst);
        rmrf(indexPathSnd);
        rmrf(indexPathThd);
        rmrf(downloadsPath);
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

    private void prepareTest() {
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        clearFolders();
    }


    @Test
    public void testMultipleSeeds() throws IOException {
        prepareTest();

        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();

        int fstPort = 11111;
        int sndPort = 11112;
        int thdPort = 11113;

        client1.start(fstPort, indexPathFst);
        client2.start(sndPort, indexPathSnd);
        client3.start(thdPort, indexPathThd);

        IOService ioService1 = new IOService(indexPathFst);
        IOService ioService2 = new IOService(indexPathSnd);
        IOService ioService3 = new IOService(indexPathThd);


        String fileName = "kitty.jpg";
        File expected = new File(basePath + fileName);
        ioService1.scatter(expected, 1024, 0);

        // move half of parts to second client
        String scatteredPathFst = indexPathFst + "/0/";
        String scatteredPathSnd = indexPathSnd + "/0/";
        File[] files = new File(scatteredPathFst).listFiles();
        Map<Integer, Set<HostPort>> seeds = new HashMap<>();
        byte[] ip = {127, 0, 0, 1};
        for (int i = 0; i < files.length; i++) {
            File fileFrom = files[i];
            if (i < files.length / 2) {
                Path from = fileFrom.toPath();

                File fileTo = new File(scatteredPathSnd + fileFrom.getName());
                fileTo.getParentFile().mkdirs();

                Path to = fileTo.toPath();
                Files.move(from, to);
            }

            Integer part = Integer.valueOf(fileFrom.getName());
            HostPort hostPort = new HostPort(ip, (i < files.length / 2) ? (sndPort) : (fstPort));
            Set<HostPort> hps = new HashSet<>();
            hps.add(hostPort);
            seeds.put(part, hps);
        }

        // prepare objects necessary for multithreading download
        FileInfo fileInfo = new FileInfo(0, fileName, expected.length());
        DownloadEnvironment downloadEnvironment = new DownloadEnvironment(seeds, indexPathThd, fileInfo);

        // download parts from two peers
        client3.downloadFromSeeds(fileInfo, downloadEnvironment);

        // combine parts
        ioService3.gather(fileInfo.getFileId(), fileInfo.getName(), downloadsPath);

        File actual = new File(downloadsPath + fileName);
        assertBinaryEquals(expected, actual);

        client1.stop();
        client2.stop();
        client3.stop();
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

}
