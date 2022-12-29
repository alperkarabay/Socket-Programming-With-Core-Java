package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class RequestHandler extends Thread {
    private Socket clientSocket;

    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            // Set up input and output streams for the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read the HTTP request header
            String headerLine;
            while ((headerLine = in.readLine()).length() != 0) {
                // Parse the HTTP request method (GET or POST)
                if (headerLine.startsWith("GET")) {
                    String[] tokens = headerLine.split(" ");
                    String path = tokens[1];



                    // Handle the GET request
                    handleGetRequest(path, out);
                } else if (headerLine.startsWith("POST")) {
                    String[] tokens = headerLine.split(" ");
                    String path = tokens[1];

                    // Read the POST request body
                    StringBuilder body = new StringBuilder();
                    while (in.ready()) {
                        body.append((char) in.read());
                    }

                    // Handle the POST request
                    handlePostRequest(path, body.toString(), out);
                }
            }

            // Close the socket
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error handling request: " + e.getMessage());

        }
    }
    public void handleGetRequest(String path, PrintWriter out){
    }

    public void handlePostRequest(String path, String body, PrintWriter out){

    }
}