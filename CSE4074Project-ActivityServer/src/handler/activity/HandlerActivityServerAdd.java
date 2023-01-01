package handler.activity;

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

public class HandlerActivityServerAdd  {

    public static void handleAddActivity(PrintWriter out, String body, String path) throws IOException {
        boolean doesExist=false;
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        if(body == null) {
            parameters = parseQuery(path);//parse the url and store the parameters in parameters map
            // if body is null, this is a get request so we will fetch the activity name from url
        }
        else{
            parameters = parseBody(body);
            //else we will fetch the activity name from body
        }try {
            doesExist = handleInsert(parameters.get("name"),doesExist); //if the activity already exists, doesExist variable will be true
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String response = "";
        // send response
        if(!doesExist){ //that if statement executes when the room doesn't exist before so it is added to database
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Activity Added</TITLE>\n" +
                    "</HEAD><BODY>Activity with name " + parameters.get("name") + " is successfully added.</BODY></HTML>";
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }
        else{
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD><BODY> Activity with name " + parameters.get("name") + " is already exists.</BODY>\n" +
                    "</HTML>";
            out.println("HTTP/1.1 403 FORBIDDEN");
            out.println("Content-Type: text/html");
            out.println("");
            out.println(response);
        }

    }
    public static boolean handleInsert(Object name, boolean doesExist) throws SQLException {
        connectDatabase(); //opened the postgresql connection
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/postgres",
                        "alper", "pass"); //get the connection
        Statement stmt = null;
        stmt = c.createStatement();
        List<String> existingActivities = new ArrayList<>();
        ResultSet rs = stmt.executeQuery( "SELECT name FROM activity;" ); // fetch the existing activities
        while ( rs.next() ) {
            String  existingName = rs.getString("name");
            existingActivities.add(existingName); //and then store them in an arraylist
        }
        for(int i=0; i<existingActivities.size(); i++)
            if(existingActivities.get(i).equals(name.toString())){
                doesExist=true; // if the activity already exists, turn the doesExist variable into true
                break;
            }
        String insertActivity = "INSERT INTO public.activity(\"name\") VALUES('" + name.toString() +"')";
        if(!doesExist) stmt.execute(insertActivity); //if activity doesn't exist insert it to database
        stmt.close();
        c.close(); //close the connection
        return doesExist;
    }



}
