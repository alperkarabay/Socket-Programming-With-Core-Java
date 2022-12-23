package handler.activity;

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

public class GetHandlerActivityServerCheck implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        boolean doesExist=false;
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parameters = parseQuery(he.getRequestURI().getQuery()); //parse the url and store the parameters in parameters map
        try {
            doesExist = handleCheck(parameters.get("name"),doesExist); //if the room already exists, doesExist variable will be true
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String response = "";
        // send response
        if(doesExist){ //that if statement executes when the room doesn't exist before so it is added to database
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Activity Exists</TITLE>\n" +
                    "</HEAD><BODY>Activity with name " + parameters.get("name") + " exists.</BODY></HTML>";
            he.sendResponseHeaders(200, response.length());
        }
        else{
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD><BODY> Activity with name " + parameters.get("name") + " not found.</BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(404, response.length());
        }
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public boolean handleCheck(Object name, boolean doesExist) throws SQLException {
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
                doesExist=true; // if the activity exists, turn the doesExist variable into true
                break;
            }
        stmt.close();
        c.close(); //close the connection
        return doesExist;
    }



}
