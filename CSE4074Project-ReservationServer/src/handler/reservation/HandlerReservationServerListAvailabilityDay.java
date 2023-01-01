package handler.reservation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static util.Parser.parseBody;
import static util.Parser.parseQuery;

public class HandlerReservationServerListAvailabilityDay{

    public static void handleGetAvailableHoursDay(PrintWriter out, String body, String path) throws IOException {
        String response = "";
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        if(body == null) {
            parameters = parseQuery(path);//parse the url and store the parameters in parameters map
            // if body is null, this is a get request so we will fetch the parameters from url
        }
        else{
            parameters = parseBody(body);
            //else we will fetch the parameters from body
        }
        if(Integer.parseInt(parameters.get("day"))>7 || Integer.parseInt(parameters.get("day"))<1) {
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY>BAD REQUEST</BODY>\n" +
                    "</HTML>";
            out.println("HTTP/1.1 400 BAD REQUEST");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
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
        out.println("HTTP/1.1 200 SUCCESS");
        out.println("Content-Type: text/html");
        out.println("");
        out.println(content);
        return;
        }


}
