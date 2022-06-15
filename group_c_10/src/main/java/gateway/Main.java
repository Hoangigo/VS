package gateway;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Serves as the entrypoint to the Gateway.
 * Parses the arguments passed in the docker-compose file
 * into the corresponding addresses and ports.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        SensorHandler[] handlers = new SensorHandler[(args.length-2)/2];
        int insert = 0;
        DatagramSocket receiver = new DatagramSocket();
        for (int i = 2; i < args.length; i += 2) {
            handlers[insert] = new SensorHandler(InetAddress.getByName(args[i]),Integer.parseInt(args[i+1]),receiver);
            ++insert;
        }
        Gateway gate = new Gateway(handlers, args[1]);
        gate.startPullingData(Integer.parseInt(args[0]));
    }
}
