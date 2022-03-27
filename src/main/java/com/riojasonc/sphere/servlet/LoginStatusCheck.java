package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.lib.util.cipher.AES;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.riojasonc.sphere.lib.BasicFunctions.getJSONFromServletRequest;
import static com.riojasonc.sphere.lib.util.Database.getIdByName;

/*
 * 判断登录状态
 */
@WebServlet(name = "LoginStatusCheck", urlPatterns = "/lsc")
public class LoginStatusCheck extends HttpServlet {
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
        PrintWriter pw = response.getWriter();
        if(requestJson.containsKey("id")) {
            String id = AES.decrypt(requestJson.getString("id"), AES.BASICKEY);
            Object obj = getIdByName(id);
            if(obj instanceof String || !id.equals(request.getSession().getAttribute("id"))) {
                responseJson.put("Status", "Error");
            }
            else {
                responseJson.put("Status", "Success");
            }
        }
        else {
            responseJson.put("Status", "Error");
        }

        pw.print(responseJson);
        pw.flush();
        pw.close();
    }
}
