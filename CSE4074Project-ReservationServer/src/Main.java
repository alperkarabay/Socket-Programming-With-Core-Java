import handler.reservation.RequestHandlerReservationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import static database.PostgreSql.connectDatabase;

public class Main {
    public static void main(String[] args) throws SQLException {
        connectDatabase();
        int portReservationServer = 9002;
        // Create a new server socket
        try (ServerSocket serverSocketReservation = new ServerSocket(portReservationServer)) {
                System.out.println("Reservation server started on port " + portReservationServer);

                // Run the server indefinitely
                while (true) {
                    // Listen for a new client connection request
                    Socket clientSocketReservation = serverSocketReservation.accept();
                    // Create a new thread to handle the request
                    Thread requestHandlerReservationServer = new RequestHandlerReservationServer(clientSocketReservation);
                    // Start the request handler thread
                    requestHandlerReservationServer.start();

                }}
            catch (IOException e) {
                System.out.println("Error starting server: " + e.getMessage());
            }

        }
}

