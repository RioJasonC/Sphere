package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.data.License;
import com.riojasonc.sphere.data.User;
import com.riojasonc.sphere.global.CONSTANT;
import com.riojasonc.sphere.lib.util.Database;
import com.riojasonc.sphere.lib.util.cipher.AES;
import com.riojasonc.sphere.lib.util.cipher.RSA;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.riojasonc.sphere.lib.BasicFunctions.flushErrorStatus;
import static com.riojasonc.sphere.lib.BasicFunctions.getJSONFromServletRequest;

@WebServlet(name = "Signup", urlPatterns = "/signup")
public class Signup extends HttpServlet {
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

        User user = new User(requestJson.getString("id"), requestJson.getString("password"), new License(requestJson.getString("cdkey")));
        try {
            user.password = AES.decrypt(user.password, RSA.decrypt(requestJson.getString("AESKey"), RSA.getPrivateKey((String) session.getAttribute("SecretKey"))));
        }
        catch (Exception e) {
            flushErrorStatus(pw, e.getMessage());
            e.printStackTrace();
            return;
        }

        int result = -1;
        try {
            result = Database.signupCheck(user);
        }
        catch (Exception e) {
            flushErrorStatus(pw, e.getMessage());
            e.printStackTrace();
            return;
        }

        if(result == CONSTANT.SUCCESS){//注册成功
            try {
                Database.signupAccount(user);
            }
            catch (Exception e) {
                flushErrorStatus(pw, e.getMessage());
                e.printStackTrace();
            }

            responseJson.put("Status", "Success");
            pw.print(responseJson);
            pw.flush();
            pw.close();
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

            flushErrorStatus(pw, msg);
        }
    }
}
