package servers;

import com.sun.net.httpserver.HttpServer;
import handler.activity.DeleteHandlerActivityServer;
import handler.activity.GetHandlerActivityServerCheck;
import handler.activity.PostHandlerActivityServerAdd;
import handler.room.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ActivityServer {
    public static void createActivityServer() throws IOException {
        int port = 9001;
        HttpServer roomServer = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Activity server started at " + port);
        roomServer.createContext("/add", new PostHandlerActivityServerAdd());
        roomServer.createContext("/remove", new DeleteHandlerActivityServer());
        roomServer.createContext("/check", new GetHandlerActivityServerCheck());

        roomServer.setExecutor(null);
        roomServer.start();
    }
}
