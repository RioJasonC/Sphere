package com.yyx.sphere.myclouddrive.servlet;

import com.yyx.sphere.global.Constant;
import com.yyx.sphere.util.UtilDatabase;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet(name="Login", urlPatterns = "/login")
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("home.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String password = request.getParameter("password");

        int result = -1;
        try {
            result = isInteger(id) ? UtilDatabase.loginCheck(Long.parseLong(id), password) : UtilDatabase.loginCheck(id, password);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if(result == Constant.Success){//登录成功
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.write("<h1>Log in successfully!!!</h1>");
            pw.flush();
        }
        else {//登录失败
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            String msg = "";
            switch (result){
                case Constant.Error.Login.front: msg = "Id or Name is invalid."; break;
                case Constant.Error.Login.password: msg = "Password is invalid."; break;
                default: msg = "Unknown Error.";
            }
            pw.write("<h1>" + msg + "</h1>");
            pw.flush();
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");//正则表达式，判断是否为整数
        return pattern.matcher(str).matches();
    }//判断是否为整数
}
