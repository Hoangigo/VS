package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private Server server;

    @BeforeEach
    void setUp() throws IOException {
        server = new Server();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.stopServer();
    }

    @Test
    void connectionsFromOtherContainer() throws InterruptedException {
        Thread.sleep(15000);
    }
}