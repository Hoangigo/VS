package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Sensor which generates random data
 * and communicates using a DatagramSocket.
 */
public class UDPSensor {
    /**
     * Maximum message Length that can be received.
     */
    private static final int BUFFER_SIZE = 512;
    /**
     * Socket to receive and send data.
     */
    private final DatagramSocket receiver;
    /**
     * Generates the data for this sensor.
     */
    private final DataGenerator generator;
    /**
     * Name of this sensor.
     */
    private final String sensorName;
    /**
     * Unit for the data this sensor produces.
     */
    private final String unit;
    /**
     * Flag to stop the thread.
     */
    private boolean running;
    /**
     * Holds the latest value the generator generated.
     */
    private float currentData;

    /**
     * Adds bytes to the message to compensate for
     * 2 byte chars such as "°".
     */
    private int twoByteBufferAddition;
    /**
     * Constructor of the UDPSensor class.
     * @param datatype Sets the unit appended to
     *                 the data this sensor produces.
     * @param name Sets the name of this sensor.
     * @param port Sets the port this sensor listens on.
     * @throws SocketException If there is a problem with the receiver.
     */
    public UDPSensor(String datatype, String name, int port) throws SocketException {
        receiver = new DatagramSocket(port);
        generator = new DataGenerator();
        sensorName = name;
        unit = datatype;
        running = true;
        currentData = 0.0f;
        twoByteBufferAddition = 0;
        if (unit.contains("°"))
            ++twoByteBufferAddition;
    }

    /**
     * Starts the generator and waits for packets
     * to be sent. If a packet arrives, it is then processed
     * and handled accordingly.
     * @throws IOException If there is a problem with the receiver.
     */
    public void startDataGeneration() throws IOException {
        generator.start();
        byte[] buffer;
        DatagramPacket request;
        while (running) {
            buffer = new byte[BUFFER_SIZE];
            request = new DatagramPacket(buffer,BUFFER_SIZE);
            receiver.receive(request);
            evaluateData(request.getData(),request.getLength(),request.getAddress(),request.getPort());
        }
        receiver.close();
    }

    /**
     * Parses the message contained in the packet received.
     * Currently, supports the message "pull" and "stop".
     * "pull" causes the sensor to reply with the value stored in currentData.
     * "stop" stops this thread.
     * @param packetBytes The bytes that make up the received message.
     * @param length The actual message length.
     * @param address Address to send the response to.
     * @param port Port to send the response to.
     * @throws IOException If there is a problem with the receiver.
     */
    private void evaluateData(byte[] packetBytes, int length, InetAddress address, int port) throws IOException {
        String decodedRequest = new String(packetBytes,0,length);
        DatagramPacket response;
        switch (decodedRequest) {
            case "pull" -> {
                currentData = generator.getData();
                String message = sensorName+", "+currentData+unit+", "+System.currentTimeMillis();
                response = new DatagramPacket(message.getBytes(),message.length()+twoByteBufferAddition,address,port);
            }
            case "stop" -> {
                generator.stopGenerator();
                running = false;
                response = new DatagramPacket("ack".getBytes(),"ack".length(),address,port);
            }
            default -> response = new DatagramPacket("error".getBytes(),"error".length(),address,port);
        }
        receiver.send(response);
    }

    public float getCurrentData() {
        return currentData;
    }
}
