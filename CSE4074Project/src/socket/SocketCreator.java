package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketCreator {
    public static void createAndStartSocket(int port){

        // Create a new server socket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            // Run the server indefinitely
            while (true) {
                // Listen for a new client connection request
                Socket clientSocket = serverSocket.accept();

                // Create a new thread to handle the request
                Thread requestHandler = new RequestHandler(clientSocket);

                // Start the request handler thread
                requestHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}

