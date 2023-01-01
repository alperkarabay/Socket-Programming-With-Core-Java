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
    public static Map<String, String> parseBody(String body) {
       Map<String,String> params = new HashMap<>();
       String bodyTokens[] = body.split("\\{");
       bodyTokens = bodyTokens[1].split("\\\n");
       String jsonTokens[];
       StringBuilder key = new StringBuilder();
       StringBuilder value = new StringBuilder();
       if(bodyTokens.length>2) {
           for (int i = 1; i < bodyTokens.length - 1; i++) {
               jsonTokens = bodyTokens[i].split(":");
               for(int j = 0; j<jsonTokens[0].length(); j++){
                   if(jsonTokens[0].charAt(j)!=' ' && jsonTokens[0].charAt(j)!='\"' && jsonTokens[0].charAt(j)!=','){
                       key.append(jsonTokens[0].charAt(j));
                   }
               }
               for(int j = 0; j<jsonTokens[1].length(); j++){
                   if(jsonTokens[1].charAt(j)!=' ' && jsonTokens[1].charAt(j)!='\"' && jsonTokens[1].charAt(j)!=','){
                       value.append(jsonTokens[1].charAt(j));
                   }
               }
               params.put(key.toString(),value.toString());
               key.setLength(0);
               value.setLength(0);
           }
       }
       return params;
    }
}
