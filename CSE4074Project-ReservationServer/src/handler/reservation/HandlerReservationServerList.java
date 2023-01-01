package handler.reservation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static handler.reservation.HandlerReservationServerListAvailabilityAll.handleGetAllAvailableHours;
import static handler.reservation.HandlerReservationServerListAvailabilityDay.handleGetAvailableHoursDay;
import static util.Parser.parseBody;
import static util.Parser.parseQuery;

public class HandlerReservationServerList  {

    public static void handleListAvailableTimes(PrintWriter out, String body, String path) throws IOException {
        Map<String, String> parameters = new HashMap<String, String>(); //url parameters
        if(body == null) {
            parameters = parseQuery(path);//parse the url and store the parameters in parameters map
            // if body is null, this is a get request so we will fetch the parameters from url
        }
        else{
            parameters = parseBody(body);
            //else we will fetch the parameters from body
        }
        if(parameters.get("day")==null) handleGetAllAvailableHours(out, body, path);
        else handleGetAvailableHoursDay(out,body,path);
    }
}
