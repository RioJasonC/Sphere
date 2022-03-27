package com.riojasonc.sphere.lib;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class BasicFunctions {
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

        JSONObject json = null;
        try {
            json = JSONObject.fromObject(sb.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return json;
    }

    /*
     * 服务器返回错误状态
     *
     * @param PrintWriter
     */
    public static void flushErrorStatus(PrintWriter pw, String str) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("Status", "Error");
        responseJson.put("ErrorType", str);
        pw.print(responseJson);
        pw.flush();
        pw.close();
    }
}
