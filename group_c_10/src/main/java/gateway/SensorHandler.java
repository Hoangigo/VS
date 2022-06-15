package gateway;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Serves as an Interface for communicating with
 * it's assigned sensor.
 */
public class SensorHandler {
    /**
     * Maximum message size in bytes.
     */
    private static final int BUFFER_SIZE = 256;
    private static final int TIMEOUT_IN_MS = 1000;
    /**
     * Address of the sensor.
     */
    private final InetAddress address;
    /**
     * Port of the sensor.
     */
    private final int port;
    /**
     * Socket to send and receive messages to the sensor.
     */
    private final DatagramSocket receiver;

    /**
     * Constructor of the SensorHandler class.
     * Assigns vital fields.
     * @param sensorAddress Address of the sensor.
     * @param sensorPort Port of the sensor.
     */
    public SensorHandler(InetAddress sensorAddress, int sensorPort, DatagramSocket receiver) {
        address = sensorAddress;
        port = sensorPort;
        this.receiver = receiver;
    }

    /**
     * Sends a message to the sensor.
     * @param msg Message to be sent.
     * @throws IOException If there is a problem with sending the message.
     */
    public void sendMessage(String msg) throws IOException {
        DatagramPacket request = new DatagramPacket(msg.getBytes(),msg.length(),address,port);
        receiver.send(request);
    }

    /**
     * Waits for the sensor to send a response.
     * @return Returns the message which was received.
     */
    public String getMessage() {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket response = new DatagramPacket(buffer, BUFFER_SIZE);
        try {
            receiver.setSoTimeout(TIMEOUT_IN_MS);
            receiver.receive(response);
        } catch (Exception e) {
            return "Package from " + address +":"+ port + "could not be received!";
        }
        return new String(response.getData(),0,response.getLength());
    }
}
