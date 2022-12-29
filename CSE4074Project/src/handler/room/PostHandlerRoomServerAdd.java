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

public class PostHandlerRoomServerAdd  {

    public static void handleAddRoom(PrintWriter out, String body, String path) throws IOException {
        boolean doesExist=false;
        String name = "";
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
        try {
            doesExist = handleInsertRoom(parameters.get("name"),doesExist); //if the room already exists, doesExist variable will be true
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String response = "";
        // send response
        if(!doesExist){ //that if statement executes when the room doesn't exist before so it is added to database
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Room Added</TITLE>\n" +
                    "</HEAD><BODY>Room with name " + parameters.get("name") + " is successfully added.</BODY></HTML>";
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }
        else{
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD><BODY> Room with name " + parameters.get("name") + " is already exists.</BODY>\n" +
                    "</HTML>";
            out.println("HTTP/1.1 403 FORBIDDEN");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }

    }
    public static boolean handleInsertRoom(Object name, boolean doesExist) throws SQLException {
        connectDatabase(); //opened the postgresql connection
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/postgres",
                        "alper", "pass"); //get the connection
        Statement stmt = null;
        stmt = c.createStatement();
        List<String> existingRooms = new ArrayList<>();
        ResultSet rs = stmt.executeQuery( "SELECT name FROM room;" ); // fetch the existing rooms
        while ( rs.next() ) {
            String  existingName = rs.getString("name");
            existingRooms.add(existingName); //and then store them in an arraylist
        }
        for(int i=0; i<existingRooms.size(); i++)
            if(existingRooms.get(i).equals(name.toString())){
                doesExist=true; // if the room already exists, turn the doesExist variable into true
                break;
            }
        String insertRoom = "INSERT INTO public.room(\"name\") VALUES('" + name.toString() +"')";
        if(!doesExist) stmt.execute(insertRoom); //if room doesn't exist insert it to database
        stmt.close();
        c.close(); //close the connection
        return doesExist;
    }

}
