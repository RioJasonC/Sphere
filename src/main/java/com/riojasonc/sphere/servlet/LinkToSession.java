package com.riojasonc.sphere.servlet;

import com.riojasonc.sphere.lib.util.cipher.RSA;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;

import static com.riojasonc.sphere.lib.BasicFunctions.flushErrorStatus;

@WebServlet(name = "LinkToSession", urlPatterns = "/lts")
public class LinkToSession extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        JSONObject responseJson = new JSONObject();
        PrintWriter pw = response.getWriter();
        HttpSession session = request.getSession();

        if(session.isNew()) { //如果会话第一次创建
            try {
                KeyPair keyPair = RSA.getKeyPair();
                String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
                String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));

                //向客户端传输RSA公钥和SessionId
                responseJson.put("Status", "Success");
                responseJson.put("ExistStatus", "New");
                responseJson.put("PublicKey", "-----BEGIN PUBLIC KEY-----\n" + publicKey + "\n-----END PUBLIC KEY-----");

                session.setAttribute("SecretKey", privateKey);
                session.setAttribute("PublicKey", publicKey);
            }
            catch (Exception e) {
                flushErrorStatus(pw, e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        else { //会话已经创建过
            responseJson.put("Status", "Success");
            responseJson.put("ExistStatus", "Already");
        }
        pw.print(responseJson);
        pw.flush();
        pw.close();
    }
}