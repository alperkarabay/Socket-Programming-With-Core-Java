package handler.reservation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static util.Parser.parseQuery;

public class PostHandlerReservationServerReserve implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        boolean isAvailable=true;
        String response = "";
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parameters = parseQuery(he.getRequestURI().getQuery()); //parse the url and store the parameters in parameters map
        //we will send a request to activity server for checking the existency of activity by using this url
        String checkActivityUrl = "http://localhost:9001/check?name=" + parameters.get("activity");
        // Create a neat value object to hold the URL
        URL url = new URL(checkActivityUrl);
        // Open a connection on the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // This line makes the request
        int activityStatus = connection.getResponseCode();

        if(activityStatus == 404){
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD><BODY> Activity with name " + parameters.get("activity") + " not found.</BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(404, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }

    }
}
