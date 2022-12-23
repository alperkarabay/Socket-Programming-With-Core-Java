package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    public static Map<String,String> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<String, String>();

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
}
