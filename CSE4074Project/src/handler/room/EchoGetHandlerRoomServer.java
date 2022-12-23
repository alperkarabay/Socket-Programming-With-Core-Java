package handler.room;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static util.Parser.parseQuery;

public class EchoGetHandlerRoomServer implements HttpHandler {
    //This class handles get requests of Room server
    @Override
    public void handle(HttpExchange he) throws IOException {
        // parse request
        Map<String, String> parameters = new HashMap<String, String>();
        URI requestedUri = he.getRequestURI();
        String query = requestedUri.getRawQuery();
        parameters=parseQuery(query);

        // send response
        String response = "";
        for (String key : parameters.keySet())
            response += key + " = " + parameters.get(key) + "\n";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());

        os.close();
    }

}
