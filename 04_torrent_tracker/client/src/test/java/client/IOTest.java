package client;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static junitx.framework.FileAssert.assertBinaryEquals;

public class IOTest {
    private final String basePath = "src/test/resources/";
    private final String indexPath = basePath + "/index/";
    private final IOService ioService = new IOService(indexPath);

    private void runScatterGather(String fileName, int fileId) throws IOException {
        runScatterGather(fileName, fileId, 2);
    }

    private void runScatterGather(String fileName, int fileId, int partSize) throws IOException {
        File expected = new File(basePath + fileName);
        ioService.scatter(expected, partSize, fileId);
        ioService.gather(fileId, fileName, basePath + "/downloads/");
        File actual = new File(basePath + "/downloads/" + fileName);
        assertBinaryEquals(expected, actual);
    }

    @Test
    public void testScatterGather() throws IOException {
        String fileName = "test.txt";
        runScatterGather(fileName, 0);
    }

    @Test
    public void testEmptyScatterGather() throws IOException {
        String fileName = "empty.txt";
        runScatterGather(fileName, 1);
    }

    @Test
    public void testBinaryScatterGather() throws IOException {
        String fileName = "kitty.jpg";
        runScatterGather(fileName, 2, 1024);
    }

    @Test(expected = FileNotFoundException.class)
    public void testScatterNotExists() throws IOException {
        String fileName = "doesnotexist.txt";
        File expected = new File(basePath + fileName);
        ioService.scatter(expected, 2, 3);
    }

    @Test(expected = FileNotFoundException.class)
    public void testGatherNotExists() throws IOException {
        String filename = "foo.txt";
        int idNotExists = 4;
        ioService.gather(idNotExists, filename, basePath + "/downloads/");
    }
}
