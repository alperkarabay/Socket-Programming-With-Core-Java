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

public class HandlerRoomServerAvailability {

    public static void handleCheckRoomAvailability(PrintWriter out, String body, String path) throws IOException {
        List<String> availableHours; //we will user this array for storing the reserved hours
        // parse request
        boolean doesExist=false;
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        if(body == null) {
            parameters = parseQuery(path);//parse the url and store the parameters in parameters map
            // if body is null, this is a get request so we will fetch parameters from url
        }
        else{
            parameters = parseBody(body);
            //else we will fetch the room name from body
        }
        String response = "";
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

        List<String> week = new ArrayList<>();
        //we will use this arraylist to converting the day number to day for information message
        week.add(" "); week.add("monday"); week.add("tuesday"); week.add("wednesday"); week.add("thursday"); week.add("friday"); week.add("saturday"); week.add("sunday");
        //we will use these variables for information message also
        String wantedDay = week.get(Integer.parseInt(parameters.get("day")));

        try {
            availableHours = handleAvailableHours(parameters.get("name"),parameters.get("day")); //get the available hours with handleAvailableHours method
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // send response
        if(availableHours.get(0).equals("Not Found")){ //that if statement executes when the room doesn't exist with given name
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> No room exists with name " + parameters.get("name") + "</BODY>\n" +
                    "</HTML>";
            out.println("HTTP/1.1 404 NOT FOUND");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }
        else {
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>SUCCESS</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY>Available hours for " + parameters.get("name") + " at day " + wantedDay + ": ";
            for(int i=0; i<availableHours.size(); i++){
                //Beautify the availableHours list and return the response message
                String[] currentAvailableHour = availableHours.get(i).split("-");
                response += currentAvailableHour[0] + ":00 - " + currentAvailableHour[1] + ":00 \n";
            }
            response += "</BODY>\n</HTML>";
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }

    }
    public static List<String> handleAvailableHours(Object name , Object day) throws SQLException {
        connectDatabase(); //opened the postgresql connection
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/postgres",
                        "alper", "pass"); //get the connection
        Statement stmt = null;
        stmt = c.createStatement();
        List<String> availableHours = new ArrayList<>();
        List<String> notAvailableHours = new ArrayList<>();
        //we will use this query to heck whether that room exists or not
        String sqlQuery = "SELECT * FROM room where name = '" + name + "';";
        ResultSet r = stmt.executeQuery(sqlQuery);
        boolean doesExist = false;
        while (r.next()) doesExist=true;
        //if it doesnt exists doestExist variable will be false so we will return the Not Found message
        if(!doesExist) {
            availableHours.add("Not Found"); // if there is not any room exists with given name this block will be executed
            return availableHours;
        }
        sqlQuery = "SELECT * FROM reservation where day = "+ day + " and room = '" + name + "';";
        ResultSet rs = stmt.executeQuery(sqlQuery); // fetch the existing rooms

        while ( rs.next() ) {
            int reservationStart = Integer.parseInt(rs.getString("hour"));
            int reservationEnd = Integer.parseInt(rs.getString("duration")) + reservationStart;
            String  reservedHours = reservationStart + "-" + reservationEnd ; //get the reserved hours
            notAvailableHours.add(reservedHours); //and then store them in an arraylist
        }

        stmt.close();
        c.close(); //close the connection
        availableHours = getAvailableHours(notAvailableHours);
        return availableHours;
    }

    public static List<String> getAvailableHours(List<String> reservedHours){
        List<String> availableHours = new ArrayList<>();
        //if there is not any reservation, available hours will simply be 0-23
        availableHours.add("0-23");
        for(int i=0; i<reservedHours.size(); i++){
            String[] tempRes = reservedHours.get(i).split("-");
            for (int j=0; j<availableHours.size(); j++){
                String[] tempAv = availableHours.get(j).split("-");
                //This if blocks work if the hours that are reserved between the jth index of available hours,
                //so we will be able to split that index of available hours
                if (Integer.parseInt(tempRes[0]) > Integer.parseInt(tempAv[0]) && Integer.parseInt(tempRes[1])<Integer.parseInt(tempAv[1])){
                    availableHours.remove(j);
                    availableHours.add(tempAv[0] + "-" + tempRes[0]);
                    availableHours.add(tempRes[1] + "-" + tempAv[1]);
                    break;
                }
                //if the start hours of reservation is equal to start hours of available hours then we should make a different split
                else if(Integer.parseInt(tempRes[0]) == Integer.parseInt(tempAv[0]) && Integer.parseInt(tempRes[1])<Integer.parseInt(tempRes[1])){
                    availableHours.remove(j);
                    availableHours.add(tempRes[1] + "-" + tempAv[1]);
                    break;
                }
            }
        }

        return availableHours;
    }
}