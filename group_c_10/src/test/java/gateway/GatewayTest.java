package gateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GatewayTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Test
    void udpConnectionTest() throws IOException {
        System.setOut(new PrintStream(outContent));
        Gateway gateway = new Gateway(new SensorHandler[]{new SensorHandler(InetAddress.getByName("sensor1"),1234,new DatagramSocket())},"server");
        gateway.broadcastToSensors("pull");
        assertNotEquals("",outContent.toString());
        gateway.stopPullingData("stop");
        System.setOut(originalOut);
    }

    @Test
    void testHTTPConnectionPerformance() throws IOException {
        int iterations = 10000;
        ArrayList<Long> rttList = new ArrayList<>();
        Socket serverConnection = new Socket(InetAddress.getByName("server"),80);
        OutputStream writer = serverConnection.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
        double average = 0.0;
        double deviation;
        double variance = 0.0;
        double median;
        String line;
        StringBuilder messages = new StringBuilder();
        String postRequest = """
                POST / HTTP/1.1\r
                Host: test\r
                Content-Type: text/plain\r
                Content-Length: 100\r
                \r
                Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean m""";
        for (int i = 0; i < iterations; ++i) {
            long start = System.nanoTime();
            writer.write(postRequest.getBytes());
            while (!(line = reader.readLine()).equals(""))
                messages.append(line);
            rttList.add(System.nanoTime()-start);
            average += (double)rttList.get(rttList.size()-1);
        }
        assertNotEquals("",messages.toString());
        average /= iterations;
        for (int i = 0; i < iterations; ++i)
            variance += (rttList.get(i)-average)*(rttList.get(i)-average)/iterations;
        deviation = Math.sqrt(variance);
        rttList.sort(Long::compare);
        median = (rttList.get(iterations/2)+rttList.get(1+iterations/2))/2.0;
        System.out.println("---------------------------------------------");
        System.out.println("Test ran with " + iterations + " requests.");
        System.out.println("\n\n");
        System.out.println("Average: " + average/1000000 + " ms");
        System.out.println("Deviation: " + deviation/1000000 + " ms");
        System.out.println("Median: " + median/1000000 + " ms");
        System.out.println("---------------------------------------------");
    }
}