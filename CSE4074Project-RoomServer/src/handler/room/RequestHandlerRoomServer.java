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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleRoot(PrintWriter out) throws IOException {
        String response = "Room Server start success if you see this message \n" + "Port: " + 9000 + "";
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("");
        out.println(response);
    }

    }
