package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.data.License;
import com.riojasonc.sphere.data.User;
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

import static com.riojasonc.sphere.lib.BasicServletFunctions.getJSONFromServletRequest;

@WebServlet(name = "Signup", urlPatterns = "/signup")
public class Signup extends HttpServlet {
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

        User user = new User(requestJson.getString("id"), requestJson.getString("password"), new License(requestJson.getString("cdkey")));
        user.password = AES.decrypt(requestJson.getString("password"), AES.keyComplete(AES.encrypt(user.name, AES.BASICKEY)));
        user.password = AES.encrypt(user.password, AES.BASICKEY0) + Hash.RSHash(user.password);

        try {
            pw = response.getWriter();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int result = -1;
        try {
            result = UtilDatabase.signupCheck(user);
        }
        catch (SQLException e) {
            responseJson.put("ResponseStatus", "Error");
            responseJson.put("ErrorType", "SQLException");
            pw.print(responseJson);
            pw.flush();
            pw.close();
            e.printStackTrace();
        }

        if(result == CONSTANT.SUCCESS){//注册成功
            try {
                UtilDatabase.signupAccount(user);
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
            responseJson.put("Status", "Success");
        }
        else {//注册失败
            String msg = null;
            switch (result){
                case CONSTANT.ERROR.SIGNUP.NAME: msg = "Name is invalid."; break;
                case CONSTANT.ERROR.SIGNUP.PASSWORD: msg = "Password is invalid."; break;
                case CONSTANT.ERROR.SIGNUP.CDKEY: msg = "CDKey is invalid."; break;
                case CONSTANT.ERROR.SIGNUP.CDKEYUSED: msg = "CDKey had been used."; break;
                case CONSTANT.ERROR.UTIL.MYSQL: msg = "Something wrong with sql."; break;
                default: msg = "Unknown Error.";
            }

            responseJson.put("ResponseStatus", "Success");
            responseJson.put("Status", "Error");
            responseJson.put("ErrorType", msg);
        }

        pw.print(responseJson);
        pw.flush();
        pw.close();
    }
}
