package com.riojasonc.sphere.servlet;

import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static com.riojasonc.sphere.lib.BasicFunctions.flushErrorStatus;
import static com.riojasonc.sphere.lib.BasicFunctions.getJSONFromServletRequest;
import static com.riojasonc.sphere.lib.util.Database.getHitokoto;

@WebServlet(name = "Hitokoto", urlPatterns = "/function/hitokoto/get")
public class Hitokoto extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        JSONObject requestJson = getJSONFromServletRequest(request);
        if(requestJson == null) {
            return;
        }
        JSONObject responseJson = new JSONObject();
        JSONObject innerRequestJson = new JSONObject();
        PrintWriter pw = response.getWriter();

        if("getMaxId".equals(requestJson.getString("mode"))) {
            innerRequestJson.put("mode", "getMaxId");
            JSONObject innerResponseJson;
            try {
                innerResponseJson = getHitokoto(innerRequestJson);
                long id = innerResponseJson.getLong("maxId");
                if(id == -1) {
                    responseJson.put("Status", "Error");
                    responseJson.put("ErrorType", "Server Internal Error");
                }
                else {
                    responseJson.put("Status", "Success");
                    responseJson.put("data", id);
                }
                pw.print(responseJson);
                pw.flush();
                pw.close();;
            }
            catch (SQLException e) {
                flushErrorStatus(pw, e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            innerRequestJson.put("mode", "getHitokoto");
            innerRequestJson.put("id", requestJson.getLong("id"));
            JSONObject innerResponseJson;
            try {
                innerResponseJson = getHitokoto(innerRequestJson);
                String hitokoto = innerResponseJson.getString("hitokoto");
                if("Error".equals(hitokoto)) {
                    responseJson.put("Status", "Error");
                    responseJson.put("ErrorType", "Server Internal Error");
                }
                else {
                    responseJson.put("Status", "Success");
                    responseJson.put("hitokoto", hitokoto);
                    responseJson.put("from_who", innerResponseJson.getString("from_who"));
                    responseJson.put("from", innerResponseJson.getString("hfrom"));
                }
                pw.print(responseJson);
                pw.flush();
                pw.close();;
            }
            catch (SQLException e) {
                flushErrorStatus(pw, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
