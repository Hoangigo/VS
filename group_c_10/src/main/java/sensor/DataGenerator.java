package sensor;

/**
 * Generates Data for the sensor.
 */
public class DataGenerator extends Thread {
    /**
     * Randomly generated float.
     */
    private float data;
    /**
     * Flag to stop this thread.
     */
    private boolean running;

    public DataGenerator() {
        data = 0.0f;
        running = true;
    }

    @Override
    public void run() {
        while (running)
            data = (float) (Math.random()*100);
    }

    public float getData() {
        return data;
    }

    public void stopGenerator() {
        running = false;
    }
}
