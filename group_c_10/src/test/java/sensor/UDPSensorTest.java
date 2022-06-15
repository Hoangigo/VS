package sensor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class UDPSensorTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void successfulUDPConnection() throws IOException {
        UDPSensor udpSensor = new UDPSensor("°C","TestSensor",1234);
        udpSensor.startDataGeneration();
        assertNotEquals(9999f,udpSensor.getCurrentData());
    }
}