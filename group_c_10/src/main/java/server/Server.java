package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server extends Thread {
    public final static int HTTP_DEFAULT_PORT = 80;
    private final ServerSocket serverSocket;
    private final List<String> container;
    private boolean running;

    public Server() throws IOException {
        serverSocket = new ServerSocket(HTTP_DEFAULT_PORT);
        container = Collections.synchronizedList(new ArrayList<>());
        running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Socket client = serverSocket.accept();
                ClientHandler handler = new ClientHandler(client, container);
                handler.start();
            }
        } catch (Exception ignored) {}
    }

    /**
     * Stops the server by terminating the loop
     * and initializing a final connection that closes
     * immediately.
     */
    public void stopServer() throws IOException {
        running = false;
        Socket s = new Socket(InetAddress.getByName("server"),HTTP_DEFAULT_PORT);
        s.close();
    }

    public boolean isRunning() {
        return running;
    }
}
