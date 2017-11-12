package com.simiyutin.javaii;

import com.sun.tools.javac.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
        System.out.println(list);
        byte [] file = client.executeGet("resources/" + list.get(0).fst); //todo return error messages
        System.out.println(new String(file));
    }
}
