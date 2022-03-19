package com.riojasonc.sphere.servlet;

import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static com.riojasonc.sphere.lib.util.UtilDatabase.getInfoById;
import static com.riojasonc.sphere.lib.BasicServletFunctions.getJSONFromServletRequest;

@WebServlet(name = "Info", urlPatterns = "/info")
public class Info extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        JSONObject requestJson = getJSONFromServletRequest(request);
        JSONObject responseJson = new JSONObject();
        PrintWriter pw = null;

        try {
            pw = response.getWriter();

            responseJson = getInfoById(requestJson);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            pw.print(responseJson);
            pw.flush();
            pw.close();
        }
    }
}
