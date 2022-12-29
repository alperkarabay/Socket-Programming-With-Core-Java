package handler.reservation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static util.Parser.parseQuery;

public class GetHandlerReservationServerListAvailabilityDay{

    public static void handle(HttpExchange he) throws IOException {
        String response = "";
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parameters = parseQuery(he.getRequestURI().getQuery()); //parse the url and store the parameters in parameters map

        if(Integer.parseInt(parameters.get("day"))>7 || Integer.parseInt(parameters.get("day"))<1 ) {
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Day value should be between 1-7 </BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(400,response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        //we will send a request to activity server for checking the existency of activity by using this url
        String checkActivityUrl = "http://localhost:9000/checkavailability?name=" + parameters.get("room")
                + "&day=" + parameters.get("day");
        // Create a neat value object to hold the URL
        URL url = new URL(checkActivityUrl);
        // Open a connection on the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // This line makes the request
        int availabilityStatus = connection.getResponseCode();
        //we read the response and save it in the StringBuffer content with the lines below
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
            he.sendResponseHeaders(availabilityStatus, content.length());
            OutputStream os = he.getResponseBody();
            os.write(content.toString().getBytes());
            os.close();
            return;
        }


}
