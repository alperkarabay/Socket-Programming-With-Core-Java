package handler.reservation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.PostgreSql.connectDatabase;
import static util.Parser.parseQuery;

public class GetHandlerReservationServerDisplay implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        // parse request
        Map<String, String> reservationInfo = new HashMap<String, String>(); //url parameters
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        String response = "";
        parameters = parseQuery(he.getRequestURI().getQuery()); //parse the url and store the parameters in parameters map
        try {
            reservationInfo = handleSelect(parameters.get("id")); //if the room already exists, doesExist variable will be true
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(reservationInfo.size() == 0){ //if reservationInfo map's size is 0, that means reservation is not found
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD><BODY> Reservation with id " + parameters.get("id") + " not found.</BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(404, response.length());
        }
        else {
            List<String> week = new ArrayList<>();
            //we will use this arraylist to converting the day number to day for information message
            week.add(" "); week.add("Monday"); week.add("Tuesday"); week.add("Wednesday"); week.add("Thursday"); week.add("Friday"); week.add("Saturday"); week.add("Sunday");
            //we will use these variables for information message also
            String wantedDay = week.get(Integer.parseInt(reservationInfo.get("day")));
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Reservation Info</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Reservation ID: " + reservationInfo.get("id") + "<BR>\n" +
                    "Room: " + reservationInfo.get("room") + "<BR>\n";
            if(reservationInfo.get("activity") != null && !reservationInfo.get("activity").equals("")) //if the reservation is made without activity, dont add the activity info
                response += "Activity: " + reservationInfo.get("activity") +  "<BR>\n";

            response += "When: " + wantedDay + " " + reservationInfo.get("hour") + ":00-" + (Integer.parseInt(reservationInfo.get("hour"))+Integer.parseInt(reservationInfo.get("duration")))
                    + ":00 </BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(200, response.length());
        }
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public Map<String,String> handleSelect(Object id) throws SQLException {
        Map<String,String> reservationInfo = new HashMap<>();
        connectDatabase(); //opened the postgresql connection
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/postgres",
                        "alper", "pass"); //get the connection
        Statement stmt = null;
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM reservation where id = " + id + ";" ); // fetch the reservation with given id
        while ( rs.next() ) {
            //put the reservation info into reservation info hashmap
            reservationInfo.put("id" , rs.getString("id"));
            reservationInfo.put("room" , rs.getString("room"));
            reservationInfo.put("day" , rs.getString("day"));
            reservationInfo.put("hour" , rs.getString("hour"));
            reservationInfo.put("duration" , rs.getString("duration"));
            reservationInfo.put("activity" , rs.getString("activity"));

        }
        return reservationInfo;
    }

}
