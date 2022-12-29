package handler.reservation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        //activityStatus will be 404 if there is no activity with the given name
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

        //we will connect to reservation api of room server with this url
        String roomReservationUrl = "http://localhost:9000/reserve?name=" + parameters.get("room") + "&day="
                + parameters.get("day") + "&hour=" + parameters.get("hour") + "&duration=" + parameters.get("duration") + "&activity=" + parameters.get("activity");
        url = new URL(roomReservationUrl);
        connection = (HttpURLConnection) url.openConnection();
        int reservationStatus = connection.getResponseCode();

        //this if block will execute only when one of the parameters is invalid. In this case room server will return 404
        if(reservationStatus == 400){
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY>BAD REQUEST</BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(400,response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }
        //this if block will execute only when the room successfully reserved. In this case room server will return 200
        else if(reservationStatus == 200){
            List<String> week = new ArrayList<>();
            //we will use this arraylist to converting the day number to day for information message
            week.add(" "); week.add("monday"); week.add("tuesday"); week.add("wednesday"); week.add("thursday"); week.add("friday"); week.add("saturday"); week.add("sunday");
            //we will use these variables for information message also
            String wantedDay = week.get(Integer.parseInt(parameters.get("day")));
            int wantedHour  =   Integer.parseInt(parameters.get("hour"));
            int wantedFinishHour = wantedHour + Integer.parseInt(parameters.get("duration"));

            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Room Reserved</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Room with name " + parameters.get("room") + " is successfully reserved at " +  wantedDay + " between " + wantedHour + ":00 - " + wantedFinishHour + ":00 </BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }
        //this if block will execute only when the room is already reserved. In this case room server will return 403
        else if (reservationStatus == 403){
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Room with name " + parameters.get("room") + " is already reserved</BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(403, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

    }
}
