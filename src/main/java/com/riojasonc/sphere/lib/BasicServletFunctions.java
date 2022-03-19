package com.riojasonc.sphere.lib;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BasicServletFunctions {
    /*
     * param HttpServletRequest
     * return JSONObject
     * throw IOException
     */
    public static JSONObject getJSONFromServletRequest(HttpServletRequest request){
        String line = null;
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return JSONObject.fromObject(sb.toString());
    }

}
