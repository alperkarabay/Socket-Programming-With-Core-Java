import handler.activity.RequestHandlerActivityServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import static database.PostgreSql.connectDatabase;

public class Main {
    public static void main(String[] args) throws SQLException {
        connectDatabase();
        int portActivityServer = 9001;
        // Create a new server socket
        try (ServerSocket serverSocketActivity = new ServerSocket(portActivityServer)) {
            System.out.println("Activity server started on port " + portActivityServer);

            // Run the server indefinitely
            while (true) {
                // Listen for a new client connection request
                Socket clientSocketActivity = serverSocketActivity.accept();
                // Create a new thread to handle the request
                Thread requestHandlerActivityServer = new RequestHandlerActivityServer(clientSocketActivity);
                // Start the request handler thread
                requestHandlerActivityServer.start();

            }}
        catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }

    }
}
