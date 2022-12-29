package handler.reservation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static util.Parser.parseQuery;

public class GetHandlerReservationServerList implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parameters = parseQuery(he.getRequestURI().getQuery());
        if(parameters.get("day")==null) GetHandlerReservationServerListAvailabilityAll.handle(he);
        else GetHandlerReservationServerListAvailabilityDay.handle(he);
    }
}
