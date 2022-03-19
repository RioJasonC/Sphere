package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.global.CONSTANT;
import com.riojasonc.sphere.lib.AES;
import com.riojasonc.sphere.lib.Hash;
import com.riojasonc.sphere.lib.util.UtilDatabase;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static com.riojasonc.sphere.lib.BasicServletFunctions.getJSONFromServletRequest;

@WebServlet(name = "Login", urlPatterns = "/login")
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        JSONObject requestJson = getJSONFromServletRequest(request);
        JSONObject responseJson = new JSONObject();
        PrintWriter pw = null;

        String id = requestJson.getString("id");
        String password = AES.decrypt(requestJson.getString("password"), AES.keyComplete(AES.encrypt(id, AES.BASICKEY)));
        password = AES.encrypt(password, AES.BASICKEY0) + Hash.RSHash(password);

        try {
            pw = response.getWriter();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int result = -1;
        try {
            result = isInteger(id) ? UtilDatabase.loginCheck(Long.parseLong(id), password) : UtilDatabase.loginCheck(id, password);
        }
        catch (SQLException e) {
            responseJson.put("ResponseStatus", "Error");
            responseJson.put("ErrorType", "SQLException");
            pw.print(responseJson);
            pw.flush();
            pw.close();
            e.printStackTrace();
        }

        responseJson.put("ResponseStatus", "Success");
        if(result == CONSTANT.SUCCESS){//登录成功
            responseJson.put("Status", "Success");
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

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");//正则表达式，判断是否为整数
        return pattern.matcher(str).matches();
    }//判断是否为整数
}