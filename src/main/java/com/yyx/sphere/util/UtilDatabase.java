package com.yyx.sphere.util;

import java.sql.*;
import com.yyx.sphere.data.License;
import com.yyx.sphere.data.User;
import com.yyx.sphere.global.Constant;

public class UtilDatabase {
    private static final String url = "jdbc:mysql://localhost:3306/sphere?useSSL=false&characterEncoding=utf8";
    private static final String user = "root";
    private static final String password = "MySQL5410";

    private static Connection connectionGet() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static int licenseActivateCheck(License license) throws SQLException{
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT status FROM license WHERE cdkey=?")){
                ps.setObject(1, license.cdkey);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) return Constant.Error.SignUp.cdkey;//cdkey不存在
                    String status = rs.getString(1);
                    return status.equals("unused")  ? Constant.Success : Constant.Error.SignUp.cdkeyused;
                }
            }
        }
    }//通行证激活判断（cdkey + status）

    public static void licenseActivate(License license) throws SQLException{
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE license SET status=? WHERE cdkey=?")){
                ps.setObject(1, "used");
                ps.setObject(2, license.cdkey);
                ps.executeUpdate();
            }
        }
    }//激活通行证（账号）

    public static int loginCheck(Object inputFront, String inputPassword) throws SQLException {
        boolean mod = inputFront instanceof Long;//true代表id,false代表name
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement(mod ? "SELECT password FROM user_info WHERE id=?" : "SELECT password FROM user_info WHERE name=?")){
                ps.setObject(1, inputFront);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) return Constant.Error.Login.front;//id或者name不存在
                    String password = rs.getString(1);
                    return inputPassword.equals(password) ? Constant.Success : Constant.Error.Login.password;//密码错误
                }
            }
        }
    }//登录判断

    public static int signupCheck(User user) throws SQLException {
        String name = user.name;
        String password = user.password;
        License license = user.license;

        int length = name.length();
        if(length == 0 || length > User.nameMaxLength) return Constant.Error.SignUp.name;
        length = password.length();
        if(length == 0 || length > User.passwordMaxLength) return Constant.Error.SignUp.password;
        return licenseActivateCheck(license);
    }//注册判断（name + password + cdkey）

    public static void signupAccount(User user) throws SQLException {
        int gender = user.gender;
        String name = user.name;
        String password = user.password;
        License license = user.license;
        String cdkey = license.cdkey;
        long id = cdkey.charAt(0) % 9 + 1;
        for(int i = 1; i < 11; i++){
            if(cdkey.charAt(i) == '-') continue;
            id = id * 10 + cdkey.charAt(i) % 10;
        }

        long cdkey_id;
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT id, usertype FROM license WHERE cdkey=?")){
                ps.setObject(1, license.cdkey);
                try(ResultSet rs = ps.executeQuery()){
                    rs.next();
                    cdkey_id = rs.getLong(1);
                    license.usertype = rs.getString(2);
                }
            }
        }//获取cdkey_id

        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO user_info (id, type, name, password, cdkey_id, gender) VALUES (?, ?, ?, ?, ?, ?)")){
                ps.setObject(1, id);
                ps.setObject(2, license.usertype);
                ps.setObject(3, name);
                ps.setObject(4, password);
                ps.setObject(5, cdkey_id);
                ps.setObject(6, gender);

                ps.executeUpdate();
            }
        }
        licenseActivate(license);
    }//注册账号
}