package handler.room;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.PostgreSql.connectDatabase;
import static util.Parser.parseBody;
import static util.Parser.parseQuery;

public class HandlerRoomServerReserve{
    public static void handleReserveRoom(PrintWriter out, String body, String path) throws IOException {
        boolean isAvailable=true;
        String response = "";
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        if(body == null) {
            parameters = parseQuery(path);//parse the url and store the parameters in parameters map
            // if body is null, this is a get request so we will fetch the room name from url
        }
        else{
            parameters = parseBody(body);
            //else we will fetch the room name from body
        }

        if(Integer.parseInt(parameters.get("day"))>7 || Integer.parseInt(parameters.get("day"))<1
                || Integer.parseInt(parameters.get("hour"))>24 || Integer.parseInt(parameters.get("duration")) > 24) {
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
        try {
            isAvailable = handleInsert(parameters.get("name"),parameters.get("day"),parameters.get("hour"), parameters.get("duration"), parameters.get("activity"), isAvailable); //if the room already exists, doesExist variable will be true
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<String> week = new ArrayList<>();
        //we will use this arraylist to converting the day number to day for information message
        week.add(" "); week.add("monday"); week.add("tuesday"); week.add("wednesday"); week.add("thursday"); week.add("friday"); week.add("saturday"); week.add("sunday");
        //we will use these variables for information message also
        String wantedDay = week.get(Integer.parseInt(parameters.get("day")));
        int wantedHour  =   Integer.parseInt(parameters.get("hour"));
        int wantedFinishHour = wantedHour + Integer.parseInt(parameters.get("duration"));
        // send response
        if(isAvailable){ //that if statement executes when the room doesn't exist before so it is added to database
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Room Reserved</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Room with name " + parameters.get("name") + " is successfully reserved at " +  wantedDay + " between " + wantedHour + ":00 - " + wantedFinishHour + ":00 </BODY>\n" +
                    "</HTML>";
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }
        else{
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Room with name " + parameters.get("name") + " is already reserved</BODY>\n" +
                    "</HTML>";
            out.println("HTTP/1.1 403 FORBIDDEN");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }
    }
    public static boolean handleInsert(Object name,Object day,Object hour,Object duration, Object activity, boolean isAvailable) throws SQLException {
        connectDatabase(); //opened the postgresql connection
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/postgres",
                        "alper", "pass"); //get the connection
        Statement stmt = null;
        stmt = c.createStatement();
        Map<String,Map<String,String>> existingReservations = new HashMap<>();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM public.reservation;" ); // fetch the existing rooms
        int i=0;
        while ( rs.next() ) {
            String  existingName = rs.getString("room");
            Map<String,String> reservations = new HashMap<>();
            reservations.put("day",rs.getString("day"));
            reservations.put("hour",rs.getString("hour"));
            reservations.put("duration",rs.getString("duration"));
            existingReservations.put(i+")"+existingName,reservations); //and then store them in an arraylist
            i++;//we are using this i to obstruct the duplicate keys in map
        }
        int wantedDay = Integer.parseInt(day.toString()); //the day that wanted to be reserved
        int wantedHour = Integer.parseInt(hour.toString()); //the hour that wanted to be reserved
        int wantedFinishHour = Integer.parseInt(duration.toString()) + wantedHour;
        for(var e : existingReservations.entrySet()){
            if(e.getKey().substring(2).equals(name.toString())){
                Map<String,String> currentRes = e.getValue();
                int reservedDay = Integer.parseInt(currentRes.get("day").toString()); //the day already reserved
                int reservedHour = Integer.parseInt(currentRes.get("hour").toString()); //the hour already reserved
                int reservationFinisHour = reservedHour + Integer.parseInt(currentRes.get("duration").toString()); //the hour that the reservation ends
                if(wantedDay == reservedDay &&
                        ( wantedHour == reservedHour ||
                                (reservationFinisHour >= wantedHour && wantedHour >= reservedHour) ||
                                (wantedFinishHour > reservedHour && wantedFinishHour < reservationFinisHour)
                        )
                ) {isAvailable = false; // if the room reserved that time make isAvailable false
                break;}
            }}
        if(activity==null) activity = "";
        String insertRoom = "INSERT INTO public.reservation(\"room\", \"day\", \"hour\" , \"duration\", \"activity\") VALUES('" + name.toString() +"' , " + wantedDay + "," + wantedHour + "," +  Integer.parseInt(duration.toString()) + ",'" + activity.toString() + "')";
        if(isAvailable) stmt.execute(insertRoom); //if room doesn't exist insert it to database
        stmt.close();
        c.close(); //close the connection
        return isAvailable;
    }

}
