package handler.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static handler.activity.HandlerActivityServerAdd.handleAddActivity;
import static handler.activity.HandlerActivityServerCheck.handleCheckActivity;
import static handler.activity.HandlerActivityServerDelete.handleRemoveActivity;

public class RequestHandlerActivityServer extends Thread{
    private Socket clientSocket;

    public RequestHandlerActivityServer(Socket socket) {
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
                    else if(path.substring(0,7).equals("/remove")) handleRemoveActivity(out,null,path);
                    else if(path.substring(0,4).equals("/add")) handleAddActivity(out,null,path);
                    else if(path.substring(0,6).equals("/check")) handleCheckActivity(out, null, path);

                } else if (headerLine.startsWith("POST")) {
                    String[] tokens = headerLine.split(" ");
                    String path = tokens[1];

                    // Read the POST request body
                    StringBuilder body = new StringBuilder();
                    while (in.ready()) {
                        body.append((char) in.read());
                    }

                    // Handle the POST request
                    if(path.substring(0,7).equals("/remove")) handleRemoveActivity(out,body.toString(),path);
                    else if(path.substring(0,4).equals("/add")) handleAddActivity(out,body.toString(),path);
                    else if(path.substring(0,6).equals("/check")) handleCheckActivity(out, body.toString(), path);



                }
            }

            // Close the socket
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error handling request: " + e.getMessage());
        }
    }

    public void handleRoot(PrintWriter out) throws IOException {
        String response = "Activity Server start success if you see this message \n" + "Port: " + 9001 + "";
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("");
        out.println(response);
    }

}