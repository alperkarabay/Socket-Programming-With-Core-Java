
import handler.room.RequestHandlerRoomServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import static database.PostgreSql.connectDatabase;


public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        connectDatabase();
        int portRoomServer = 9000;
        // Create a new server socket
        try (ServerSocket serverSocketRoom = new ServerSocket(portRoomServer)) {
            System.out.println("Room server started on port " + portRoomServer);
                // Run the server indefinitely
            while (true) {
                // Listen for a new client connection request
                // Create a new thread to handle the request
                // Start the request handler thread
                Socket clientSocketRoom = serverSocketRoom.accept();
                Thread requestHandlerRoomServer = new RequestHandlerRoomServer(clientSocketRoom);

                requestHandlerRoomServer.start();
            }}
            catch (IOException e) {
                System.out.println("Error starting server: " + e.getMessage());
            }

        }
    }
