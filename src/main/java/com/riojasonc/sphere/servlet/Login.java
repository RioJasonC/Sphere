package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.global.CONSTANT;
import com.riojasonc.sphere.lib.util.cipher.AES;
import com.riojasonc.sphere.lib.util.cipher.RSA;
import com.riojasonc.sphere.lib.util.Database;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static com.riojasonc.sphere.lib.BasicFunctions.flushErrorStatus;
import static com.riojasonc.sphere.lib.BasicFunctions.getJSONFromServletRequest;
import static com.riojasonc.sphere.lib.util.Database.getIdByName;

@WebServlet(name = "Login", urlPatterns = "/login")
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
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
        HttpSession session = request.getSession();
        if(session.isNew()) {
            return;
        }
        Object obj = getIdByName(requestJson.getString("id"));
        if(obj instanceof String) {
            flushErrorStatus(pw, "Id or Name is invalid.");
            return;
        }

        String id = String.valueOf(obj);
        String password = requestJson.getString("password");
        try {
            password = AES.decrypt(password, RSA.decrypt(requestJson.getString("AESKey"), RSA.getPrivateKey((String) session.getAttribute("SecretKey"))));
        }
        catch (Exception e) {
            flushErrorStatus(pw, e.getMessage());
            e.printStackTrace();
            return;
        }

        int result;
        try {
            result = Database.loginCheck(Long.parseLong(id), password);
        }
        catch (SQLException e) {
            flushErrorStatus(pw, e.getMessage());
            e.printStackTrace();
            return;
        }

        if(result == CONSTANT.SUCCESS){//登录成功
            responseJson.put("Status", "Success");
            responseJson.put("id", AES.encrypt(id, AES.BASICKEY));

            //session保存对应的id
            session.setAttribute("id", id);
        }
        else {//登录失败
            String msg;
            switch (result){
                case CONSTANT.ERROR.LOGIN.FRONT: msg = "Id or Name is invalid."; break;
                case CONSTANT.ERROR.LOGIN.PASSWORD: msg = "Password is invalid."; break;
                case CONSTANT.ERROR.UTIL.MYSQL: msg = "Something wrong with sql."; break;
                default: msg = "Unknown Error.";
            }
            responseJson.put("Status", "Error");
            responseJson.put("ErrorType", msg);
        }
        pw.print(responseJson);
        pw.flush();
        pw.close();
    }
}