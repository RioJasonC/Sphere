package com.riojasonc.sphere.myclouddrive.servlet;

import com.riojasonc.sphere.data.License;
import com.riojasonc.sphere.data.User;
import com.riojasonc.sphere.global.Constant;
import com.riojasonc.sphere.util.UtilDatabase;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "Signup", urlPatterns = "/signup")
public class Signup extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("signup.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        User user = new User(request.getParameter("name"), request.getParameter("password"), new License(request.getParameter("cdkey")));

        int result = -1;
        try {
            result = UtilDatabase.signupCheck(user);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if(result == Constant.Success){//注册成功
            try {
                UtilDatabase.signupAccount(user);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            response.sendRedirect("signups.jsp");
        }
        else {//注册失败
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            String msg = "";
            switch (result){
                case Constant.Error.SignUp.name: msg = "Name is invalid."; break;
                case Constant.Error.SignUp.password: msg = "Password is invalid."; break;
                case Constant.Error.SignUp.cdkey: msg = "CDKey is invalid."; break;
                case Constant.Error.SignUp.cdkeyused: msg = "CDKey had been used."; break;
                default: msg = "Unknown Error.";
            }
            pw.write("<h1>" + msg + "</h1>");
            pw.flush();
        }
    }
}
