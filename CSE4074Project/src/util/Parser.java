package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    public static Map<String,String> parseQuery(String query) throws UnsupportedEncodingException {
        //we parse the url in this method and returning the parameters as map
        Map<String, String> result = new HashMap<String, String>();
        String splitPath[]  = query.split("\\?");
        query = splitPath[1];
        for (String param : query.split("&")) {

            String pair[] = param.split("=");

            if (pair.length>1) {

                result.put(pair[0], pair[1]);

            }else{

                result.put(pair[0], "");

            }

        }

        return result;
    }
    public static Map<String,String> parseBody(String body) throws UnsupportedEncodingException{
        Map<String, String> params = new HashMap<>();
        for (String param : body.split("&")) {
            String[] tokens = param.split("=");
            String key = URLDecoder.decode(tokens[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(tokens[1], StandardCharsets.UTF_8);
            params.put(key, value);
        }
        return params;
    }
}
