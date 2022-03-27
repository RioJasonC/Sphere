package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.lib.util.cipher.AES;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "EdcryptKeyGet", urlPatterns = "/function/edcrypt")
public class Edcrypt extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String text = request.getParameter("text");
        String result = AES.keyComplete(AES.encrypt(text, AES.BASICKEY));

        PrintWriter pw = response.getWriter();
        pw.write(result);
    }
}
