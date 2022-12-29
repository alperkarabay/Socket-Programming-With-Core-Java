package handler.room;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.PostgreSql.connectDatabase;
import static handler.room.HandlerRoomServerAdd.handleAddRoom;
import static handler.room.HandlerRoomServerAvailability.handleCheckRoomAvailability;
import static handler.room.HandlerRoomServerDelete.handleDeleteRoom;
import static handler.room.HandlerRoomServerReserve.handleReserveRoom;
import static util.Parser.parseQuery;

public class RequestHandlerRoomServer extends Thread{
    private Socket clientSocket;

    public RequestHandlerRoomServer(Socket socket) {
        this.clientSocket = socket;
    }
    public void run() {
        try {
            // Set up input and output streams for the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read the HTTP request header
            String headerLine;
            while ((headerLine = in.readLine()).length() != 0) {
                // Parse the HTTP request method (GET or POST)
                if (headerLine.startsWith("GET")) {
                    String[] tokens = headerLine.split(" ");
                    String path = tokens[1];

                    // Handle the GET request
                    if (path.equals("/")) handleRoot(out);
                    else if(path.substring(0,4).equals("/add")) handleAddRoom(out, null , path);
                    else if(path.substring(0,7).equals("/remove")) handleDeleteRoom(out,null,path);
                    else if(path.substring(0,8).equals("/reserve")) handleReserveRoom(out,null,path);
                    else if(path.substring(0,18).equals("/checkavailability")) handleCheckRoomAvailability(out,null,path);

                } else if (headerLine.startsWith("POST")) {
                    String[] tokens = headerLine.split(" ");
                    String path = tokens[1];

                    // Read the POST request body
                    StringBuilder body = new StringBuilder();
                    while (in.ready()) {
                        body.append((char) in.read());
                    }

                    // Handle the POST request
                    if(path.substring(0,4).equals("/add")) handleAddRoom(out,body.toString(), path);
                    else if(path.substring(0,7).equals("/remove")) handleDeleteRoom(out,body.toString(),path);
                    else if(path.substring(0,8).equals("/reserve")) handleReserveRoom(out,body.toString(),path);
                    else if(path.substring(0,18).equals("/checkavailability")) handleCheckRoomAvailability(out,body.toString(),path);



                }
            }

            // Close the socket
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error handling request: " + e.getMessage());
        }
    }

    public void handleRoot(PrintWriter out) throws IOException {
        String response = "Room Server start success if you see this message \n" + "Port: " + 9000 + "";
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("");
        out.println(response);
    }

    public void handleDelete(HttpExchange he) throws IOException {
        boolean doesExist=false;
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parameters = parseQuery(he.getRequestURI().getQuery()); //parse the url and store the parameters in parameters map
        try {
            doesExist = handleDelete(parameters.get("name"),doesExist); //if the room already exists, doesExist variable will be true
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String response = "";
        // send response
        if(doesExist){ //that if statement executes when the room exists before so it is deleted from database
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Room Removed Successfully</TITLE>\n" +
                    "</HEAD>\n" +
                    "<BODY> Room with name" + parameters.get("name") + " is successfully removed </BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(200, response.length());
        }
        else{
            response = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<TITLE>Error</TITLE>\n" +
                    "</HEAD> <BODY> Room with name " + parameters.get("name") + " is not found. </BODY>\n" +
                    "</HTML>";
            he.sendResponseHeaders(403, response.length());
        }
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public boolean handleDelete(Object name, boolean doesExist) throws SQLException {
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
        String deleteRoom = "DELETE FROM public.room WHERE name = '" + name.toString() +"'";
        if(doesExist) stmt.execute(deleteRoom); //if room exists delete it from database
        stmt.close();
        c.close(); //close the connection
        return doesExist;
    }
}
