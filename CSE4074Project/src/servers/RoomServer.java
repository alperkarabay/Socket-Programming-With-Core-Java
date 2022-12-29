package servers;

import com.sun.net.httpserver.HttpServer;
import handler.room.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static socket.SocketCreator.createAndStartSocket;

public class RoomServer {
    public static void createRoomServer() throws IOException {
        int port = 9000;
        // Create a new server socket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Room server started on port " + port);

            // Run the server indefinitely
            while (true) {
                // Listen for a new client connection request
                Socket clientSocket = serverSocket.accept();

                // Create a new thread to handle the request
                Thread requestHandler = new RequestHandlerRoomServer(clientSocket);

                // Start the request handler thread
                requestHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
      /*
        roomServer.createContext("/remove", new DeleteHandlerRoomServer());
        roomServer.createContext("/reserve", new PostHandlerRoomServerReserve());
        roomServer.createContext("/checkavailability", new GetHandlerRoomServerAvailability());

        roomServer.setExecutor(null);
        roomServer.start();*/
}

