package handler.room;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RootHandlerRoomServer {
    //This class displays the server status
    public void handle(HttpExchange he) throws IOException {
        String response = "<h1>Room Server start success if you see this message</h1>" + "<h1>Port: " + 9000 + "</h1>";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
