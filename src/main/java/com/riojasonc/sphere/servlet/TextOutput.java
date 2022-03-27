package com.riojasonc.sphere.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "TextOutput", urlPatterns = "/function/text/output")
public class TextOutput extends HttpServlet {
    /*
    * @param text=?
    *
    * @return 输出参数字符串
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String text = request.getParameter("text");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.write(text);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doGet(request, response);
    }
}