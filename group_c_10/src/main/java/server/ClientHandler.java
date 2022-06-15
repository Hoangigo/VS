package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    public static final String REQUEST_CANNOT_BE_PROCESSED = "500 Request cannot be processed.";
    public static final String BAD_REQUEST = "400 Request is missing Content-Length or Content-Type is not text/plain";
    public static final String DEFAULT_ANSWER = "200 OK";
    public static final int CONTENT_LENGTH_BEGIN_INDEX = 16;
    private static final int CONTENT_TYPE_BEGIN_INDEX = 14;

    private final Socket connection;

    private final List<String> sharedContainer;

    private final BufferedReader reader;

    private final OutputStream writer;

    private int contentLength;

    private String contentType;

    private String statusMessage;

    public ClientHandler(Socket clientConnection, List<String> container) throws IOException {
        connection = clientConnection;
        sharedContainer = container;
        reader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        writer = clientConnection.getOutputStream();
    }

    @Override
    public synchronized void run() {
        try {
            String bodyContent;
            while (!connection.isClosed()) {
                parseHeader();
                bodyContent = getBody();
                if (bodyContent == null)
                    break;
                sharedContainer.add(bodyContent);
                sendAnswer();
            }
            System.out.println(sharedContainer);
        } catch (IOException ignored) {}
    }

    private void parseHeader() throws IOException {
        statusMessage = DEFAULT_ANSWER;
        contentLength = -1;
        contentType = "NOT OKAY";
        String data;
        if (!requestLineIsValid(reader.readLine())) {
            contentLength = 0;
            statusMessage = REQUEST_CANNOT_BE_PROCESSED;
            sendAnswer();
            return;
        }
        data = reader.readLine();
        while (data != null && !data.equals("")) {
            if (data.startsWith("Content-Length: "))
                contentLength = getContentLength(data);
            if (data.startsWith("Content-Type: "))
                contentType = getContentType(data);
            data = reader.readLine();
        }
        if (requestMissesProperHeaderFields()) {
            contentLength = 0;
            statusMessage = BAD_REQUEST;
            sendAnswer();
        }
    }

    private String getBody() throws IOException {
        if (contentLength == 0)
            return null;
        StringBuilder data = new StringBuilder();
        while(contentLength > 0) {
            int i = reader.read();
            data.append((char)i);
            --contentLength;
        }
        return data.toString();
    }

    private void sendAnswer() throws IOException {
        String http = "HTTP/1.1 " + statusMessage + "\r\n"
                + "Server: dataStorage9000\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: 0\r\n\r\n";
        writer.write(http.getBytes());
    }

    private String getContentType(String data) {
        try {
            return data.substring(CONTENT_TYPE_BEGIN_INDEX);
        } catch (Exception ignored) {}
        return "NOT OKAY";
    }

    private int getContentLength(String data) {
        try {
            return Integer.parseInt(data.substring(CONTENT_LENGTH_BEGIN_INDEX));
        } catch (Exception ignored) {}
        return -1;
    }

    private boolean requestLineIsValid(String data) {
        if (data == null)
            return false;
        data = data.toUpperCase();
        String[] tokens = data.split(" ");
        if (tokens.length != 3)
            return false;
        return tokens[0].equals("POST") && tokens[1].equals("/") && tokens[2].equals("HTTP/1.1");
    }

    private boolean requestMissesProperHeaderFields() {
        return contentLength == -1 || !contentType.equals("text/plain");
    }

}
