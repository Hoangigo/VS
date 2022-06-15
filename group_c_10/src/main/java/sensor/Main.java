package sensor;

import java.io.IOException;

/**
 * Serves as the entrypoint for the sensor.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        UDPSensor udpsensor = new UDPSensor(args[0],args[1],Integer.parseInt(args[2]));
        udpsensor.startDataGeneration();
    }
}
