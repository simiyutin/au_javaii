package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MainTest {

    private void simpleTestClient(Client client) throws IOException {
        List<Pair<String, Boolean>> list = client.executeList("resources");
        list.sort(Comparator.comparing(p -> p.fst));
        assertEquals(list, Arrays.asList(new Pair<>("dir", true), new Pair<>("empty", false), new Pair<>("test_file.txt", false)));
        byte [] file = client.executeGet("resources/test_file.txt");
        assertEquals("test file text!", new String(file));
    }

    @Test
    public void testSingleClient() throws IOException {
        Server server = new Server();
        server.start(11111);

        Client client = new Client();
        assertFalse(client.isConnected());
        client.connect("localhost", 11111);
        assertTrue(client.isConnected());
        simpleTestClient(client);
        client.disconnect();
        assertFalse(client.isConnected());
        server.stop();
    }

    @Test
    public void testTwoClients() throws IOException {
        Server server = new Server();
        server.start(11112);

        Client client = new Client();
        client.connect("localhost", 11112);
        simpleTestClient(client);

        Client client2 = new Client();
        client2.connect("localhost", 11112);
        simpleTestClient(client2);

        client.disconnect();
        client2.disconnect();
        server.stop();
    }

    @Test
    public void testServer() throws IOException, InterruptedException {
        Server server = new Server();
        server.start(11113);

        Client client = new Client();
        client.connect("localhost", 11113);
        simpleTestClient(client);

        server.stop();
        TimeUnit.MILLISECONDS.sleep(500);

        Client client2 = new Client();
        try {
            client2.connect("localhost", 11113);
        } catch (IOException e) {
            return;
        }
        fail("Exception must be thrown");
    }

    @Test
    public void testEmptyFile() throws IOException {
        Server server = new Server();
        server.start(11114);
        Client client = new Client();
        client.connect("localhost", 11114);
        byte [] bytes = client.executeGet("resources/empty");
        assertEquals(bytes.length, 0);
    }
}
