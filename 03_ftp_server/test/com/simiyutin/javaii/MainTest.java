package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {
    @Test
    public void testSimple() throws IOException {
        new Thread(() -> {
            Server server = new Server();
            try {
                server.start(11111);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Client client = new Client();
        client.connect("localhost", 11111);
        List<Pair<String, Boolean>> list = client.executeList("resources");
        assertEquals(list, Collections.singletonList(new Pair<>("test_file.txt", false)));
        byte [] file = client.executeGet("resources/" + list.get(0).fst); //todo return error messages
        assertEquals("test file text!", new String(file));
        client.disconnect();
    }
}
