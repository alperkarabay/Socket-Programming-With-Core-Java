package servers;

import com.sun.net.httpserver.HttpServer;
import handler.activity.DeleteHandlerActivityServer;
import handler.activity.GetHandlerActivityServerCheck;
import handler.activity.PostHandlerActivityServerAdd;
import handler.reservation.GetHandlerReservationServerDisplay;
import handler.reservation.GetHandlerReservationServerList;
import handler.reservation.GetHandlerReservationServerListAvailabilityDay;
import handler.reservation.PostHandlerReservationServerReserve;
import handler.room.RootHandlerRoomServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ReservationServer{
    public static void createReservationServer() throws IOException {
        int port = 9002;
        HttpServer roomServer = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Reservation server started at " + port);
        roomServer.createContext("/", new RootHandlerRoomServer());
        roomServer.createContext("/add", new PostHandlerActivityServerAdd());
        roomServer.createContext("/reserve", new PostHandlerReservationServerReserve());
        roomServer.createContext("/check", new GetHandlerActivityServerCheck());
        roomServer.createContext("/listavailability", new GetHandlerReservationServerList());
        roomServer.createContext("/display", new GetHandlerReservationServerDisplay());


        roomServer.setExecutor(null);
        roomServer.start();
    }
}
