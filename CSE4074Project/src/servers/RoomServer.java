package servers;

import com.sun.net.httpserver.HttpServer;
import handler.room.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RoomServer {
    public static void createRoomServer() throws IOException {
        int port = 9000;
        HttpServer roomServer = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Room server started at " + port);
        roomServer.createContext("/", new RootHandlerRoomServer());
        roomServer.createContext("/add", new PostHandlerRoomServerAdd());
        roomServer.createContext("/remove", new DeleteHandlerRoomServer());
        roomServer.createContext("/reserve", new PostHandlerRoomServerReserve());
        roomServer.createContext("/checkavailability", new GetHandlerRoomServerAvailability());

        roomServer.setExecutor(null);
        roomServer.start();
    }
}
