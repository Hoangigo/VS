package gateway;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Gateway class connects the
 * different parts of this distributed system
 * by communicating with sensors, adapters and servers.
 */
public class Gateway {
    public static final int HTTP_DEFAULT_PORT = 80;
    /**
     * Holds all sensorHandlers of the gateway
     * which provide an interface for fetching
     * data from sensors.
     */
    private final SensorHandler[] handlers;
    /**
     * Periodically pulls data from the sensors.
     */
    private final Timer mainTimer;

    /**
     * TCP-Socket for communication between gateway and server.
     */
    private final Socket connection;

    /**
     * Reads from the input stream of the Socket.
     */
    private final BufferedReader reader;

    /**
     * Writes to the output stream of the Socket.
     */
    private final OutputStream writer;

    /**
     * Constructor of the Gateway class.
     * @param handlers All handlers the gateway needs.
     */
    public Gateway(SensorHandler[] handlers, String serverName) throws IOException {
        this.connection = new Socket(InetAddress.getByName(serverName), HTTP_DEFAULT_PORT);
        this.mainTimer = new Timer();
        this.handlers = handlers;
        this.reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
        this.writer = this.connection.getOutputStream();
    }

    /**
     * Starts the mainTimer to periodically pull
     * data from the sensors.
     * @param delay Sets the delay for each pull.
     */
    public void startPullingData(int delay) {
        mainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                broadcastToSensors("pull");
            }
        }, 0, delay);
    }

    /**
     * Stops requesting data from the sensors.
     * additionally, sends a message to all attached sensors.
     *
     * @param msg message for the sensors.
     */
    public void stopPullingData(String msg) throws IOException {
        mainTimer.cancel();
        connection.close();
        broadcastToSensors(msg);
    }

    /**
     * Sends a message to each sensor
     * via it's assigned handler.
     * @param msg message to be sent to the sensors.
     * Usually "pull" to get data. "stop" stops the sensor.
     */
    public void broadcastToSensors(String msg) {
        try {
            for (SensorHandler handler : handlers) {
                if (handler != null) {
                    handler.sendMessage(msg);
                    sendPostRequest(handler.getMessage());
                    getServerResponse();
                }
            }
        } catch (IOException ignored) {}
    }

    /**
     * Sends a Post request to the server
     * that contains the sensor data.
     * @param message Sensor data.
     */
    private void sendPostRequest(String message) throws IOException {
        String postRequest = "POST / HTTP/1.1\r\n"
                + "Host: gateway\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: " + message.length()+ "\r\n"
                +"\r\n" + message;
        writer.write(postRequest.getBytes());
    }

    /**
     * Prints the server response to standard output.
     */
    private void getServerResponse() throws IOException {
        String line;
        while (!(line = reader.readLine()).equals(""))
            System.out.println(line);
    }
}
