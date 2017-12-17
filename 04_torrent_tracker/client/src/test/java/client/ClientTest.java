package client;

import junitx.framework.Assert;
import junitx.framework.AssertionFailedError;
import org.junit.Test;
import requests.FileInfo;
import requests.HostPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
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
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clearFolders();
    }

    @Test
    public void startNoTracker() throws IOException {
        prepareTest();
        Client client = new Client(11111, "localhost", indexPathFst);
        client.stop();
    }
}
