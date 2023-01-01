package handler.reservation;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static util.Parser.parseBody;
import static util.Parser.parseQuery;

public class HandlerReservationServerListAvailabilityAll {
    public static void handleGetAllAvailableHours(PrintWriter out, String body, String path) throws IOException {
        String response = "";
        // parse request
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        if(body == null) {
            parameters = parseQuery(path);//parse the url and store the parameters in parameters map
            // if body is null, this is a get request so we will fetch the parameters from url
        }
        else{
            parameters = parseBody(body);
            //else we will fetch the parameters from body
        }
        int availabilityStatus=0;
        StringBuffer content = new StringBuffer();

        content.append("<HTML>\n" +
                "<HEAD>\n" +
                "<TITLE>SUCCESS</TITLE>\n" +
                "</HEAD>\n" +
                "<BODY>\n" +
                "<ul>");
        for(int i=1; i<8; i++){
            //in this for loop we will send requests to room servers checkAvailability api for each day of the week
            String checkActivityUrl = "http://localhost:9000/checkavailability?name=" + parameters.get("room")
                    + "&day=" + i;

            // Create a neat value object to hold the URL
            URL url = new URL(checkActivityUrl);
            // Open a connection on the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // This line makes the request
            availabilityStatus = connection.getResponseCode();
            //we read the response and save it in the StringBuffer content with the lines below
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(in.equals("<TITLE>Error</TITLE>")){
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
                    return;
                }
                if(!inputLine.equals("<HTML>") && !inputLine.equals("<HEAD>") && !inputLine.equals("<HEAD>") && !inputLine.equals("<TITLE>SUCCESS</TITLE>")
                        && !inputLine.equals("</HEAD>") && !inputLine.equals("</HTML>") && !inputLine.equals("</BODY>")){
                    if(inputLine.substring(0,6).equals("<BODY>")){
                        content.append("<li>" + inputLine.substring(6));
                    }
                    else
                        content.append(inputLine);
                }
            }

            in.close();
            connection.disconnect();
        }
        content.append("</ul>\n</BODY>\n</HTML>");
        out.println("HTTP/1.1 200 SUCCESS");
        out.println("Content-Type: text/html");
        out.println("");
        out.println(content);
        return;
    }
}
