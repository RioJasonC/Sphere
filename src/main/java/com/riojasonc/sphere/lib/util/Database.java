package com.riojasonc.sphere.lib.util;

import java.sql.*;
import com.riojasonc.sphere.data.License;
import com.riojasonc.sphere.data.User;
import com.riojasonc.sphere.global.CONSTANT;
import net.sf.json.JSONObject;

public class Database {
    private static final String user = "root";
    private static final String Driver = "com.mysql.cj.jdbc.Driver";

    private static final String url = "jdbc:mysql://localhost/sphere?useSSL=false&characterEncoding=utf8";
    private static final String password = "";

    private final boolean blabla = init();

    private boolean init(){
        try{
            Class.forName(Driver);
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return true;
    }

    private static Connection connectionGet() throws SQLException{
        return DriverManager.getConnection(url, user, password);
    }

    public static int licenseActivateCheck(License license) throws SQLException{
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT status FROM license WHERE cdkey=?")){
                ps.setObject(1, license.cdkey);

                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) {
                        return CONSTANT.ERROR.SIGNUP.CDKEY;//cdkey不存在
                    }
                    String status = rs.getString(1);
                    return "unused".equals(status)  ? CONSTANT.SUCCESS : CONSTANT.ERROR.SIGNUP.CDKEYUSED;
                }
            }
        }
    } //通行证激活判断（cdkey + status）

    public static void licenseActivate(License license) throws SQLException{
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE license SET status=? WHERE cdkey=?")){
                ps.setObject(1, "used");
                ps.setObject(2, license.cdkey);
                ps.executeUpdate();
            }
        }
    } //激活通行证（账号）

    public static int loginCheck(Long id, String inputPassword) throws SQLException {
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT password FROM user_info WHERE id=?")){
                ps.setObject(1, id);

                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) {
                        return CONSTANT.ERROR.LOGIN.FRONT; //id不存在
                    }
                    String password = rs.getString(1);
                    return inputPassword.equals(password) ? CONSTANT.SUCCESS : CONSTANT.ERROR.LOGIN.PASSWORD;//密码错误
                }
            }
        }
    } //登录判断

    public static int signupCheck(User user) throws SQLException {
        String name = user.name;
        String password = user.password;
        License license = user.license;

        int length = name.length();
        if(length == 0 || length > User.nameMaxLength) {
            return CONSTANT.ERROR.SIGNUP.NAME;
        }
        length = password.length();
        if(length == 0 || length > User.passwordMaxLength) {
            return CONSTANT.ERROR.SIGNUP.PASSWORD;
        }
        return licenseActivateCheck(license);
    } //注册判断（name + password + cdkey）

    public static void signupAccount(User user) throws SQLException {
        int gender = user.gender;
        String name = user.name;
        String password = user.password;
        License license = user.license;
        String cdkey = license.cdkey;
        long id = cdkey.charAt(0) % 9 + 1;
        for(int i = 1; i < 11; i++){
            if(cdkey.charAt(i) == '-') {
                continue;
            }
            id = id * 10 + cdkey.charAt(i) % 10;
        }

        long cdkey_id;
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT id, userType FROM license WHERE cdkey=?")){
                ps.setObject(1, license.cdkey);
                try(ResultSet rs = ps.executeQuery()){
                    rs.next();
                    cdkey_id = rs.getLong(1);
                    license.userType = rs.getString(2);
                }
            }
        } //获取cdkey_id

        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO user_info (id, type, name, password, cdkey_id, gender) VALUES (?, ?, ?, ?, ?, ?)")){
                ps.setObject(1, id);
                System.out.println(name);
                ps.setObject(2, license.userType);
                ps.setObject(3, name);
                ps.setObject(4, password);
                ps.setObject(5, cdkey_id);
                ps.setObject(6, gender);

                ps.executeUpdate();
            }
        }
        licenseActivate(license);
    } //注册账号

    /*
     * 通过用户昵称获取id
     *
     * @param 用户名或id
     * @return Object 如果成功则返回Long型，失败则返回String型
     */
    public static Object getIdByName(String name){
        try(Connection conn = connectionGet()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT id FROM user_info WHERE name=?")) {
                ps.setObject(1, name);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return rs.getLong(1);
                    }
                    else {
                        PreparedStatement ps0 = conn.prepareStatement("SELECT * FROM user_info WHERE id=?");
                        ps0.setObject(1, name);
                        ResultSet rs0 = ps0.executeQuery();

                        return rs0.next() ? Long.parseLong(name) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE;
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return CONSTANT.SERVER_RESPONSE.DATA.FAILURE;
    } //通过用户昵称获取Id

    /* 获取信息
     * @param JSONObject 需要获取的数据
     * @return JSONObject 对应的数据
     */
    public static JSONObject getInfoById(JSONObject json) throws SQLException {
        Object id = getIdByName(json.getString("id"));
        JSONObject responseJson = new JSONObject();

        if(id instanceof String) {
            responseJson.put("Status", "Error");
            responseJson.put("ErrorType", "InvalidParam"); //无效的参数
            return responseJson;
        }

        try(Connection conn = connectionGet()) {
            String[] q = json.getString("q").split(",");

            //尝试获取cdkey_id并判断请求是否有效
            Long cdkey_id = null;
            try(PreparedStatement ps = conn.prepareStatement("SELECT cdkey_id FROM user_info WHERE id=?")) {
                ps.setObject(1, id);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        cdkey_id = rs.getLong(1);
                    }
                    else {
                        responseJson.put("Status", "Error");
                        responseJson.put("ErrorType", "InvalidParam"); //无效的参数
                        return responseJson;
                    }
                }
            }
            responseJson.put("Status", "Success");

            for(int i = 0; i < q.length; i++) {
                String[] str = q[i].split(".");

                switch(str[0]) {
                    case "user_info": {
                        switch(str[1]) {
                            case "id": {
                                responseJson.put(q[i], id);
                            } break;
                            case "type": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT type FROM user_info WHERE id=?")) {
                                    ps.setObject(1, id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            case "name": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT name FROM user_info WHERE id=?")) {
                                    ps.setObject(1, id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            case "cdkey_id": {
                                responseJson.put(q[i], cdkey_id);
                            }
                            case "cdkey": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT cdkey FROM license WHERE id=?")) {
                                    ps.setObject(1, cdkey_id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            case "gender": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT gender FROM user_info WHERE id=?")) {
                                    ps.setObject(1, id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            default: {
                                responseJson.put(q[i], CONSTANT.SERVER_RESPONSE.DATA.INVALID);
                            }
                        }
                    } break;
                    case "license": {
                        switch(str[1]) {
                            case "id": {
                                responseJson.put(q[1], cdkey_id);
                            } break;
                            case "cdkey": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT cdkey FROM license WHERE id=?")) {
                                    ps.setObject(1, cdkey_id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            case "userType": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT userType FROM license WHERE id=?")) {
                                    ps.setObject(1, cdkey_id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            case "status": {
                                try(PreparedStatement ps = conn.prepareStatement("SELECT status FROM license WHERE id=?")) {
                                    ps.setObject(1, cdkey_id);

                                    try(ResultSet rs = ps.executeQuery()) {
                                        responseJson.put(q[i], rs.next() ? rs.getString(1) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE);
                                    }
                                }
                            } break;
                            default: {
                                responseJson.put(q[i], CONSTANT.SERVER_RESPONSE.DATA.INVALID);
                            }
                        }
                    } break;
                    default: {
                        responseJson.put(q[i], CONSTANT.SERVER_RESPONSE.DATA.INVALID);
                    }
                }
            }
        }
        return responseJson;
    }

    /* 获取Hitokoto
     * @return JSONObject
     */
    public static JSONObject getHitokoto(JSONObject requestJSON) throws SQLException {
        JSONObject responseJson = new JSONObject();

        try(Connection conn = connectionGet()){
            if("getHitokoto".equals(requestJSON.getString("mode"))) {
                long id = requestJSON.getLong("id");
                try(PreparedStatement ps = conn.prepareStatement("SELECT hitokoto, from_who, hfrom FROM hitokoto WHERE id=?")){
                    ps.setObject(1, Long.toString(id));
                    try(ResultSet rs = ps.executeQuery()){
                        if(!rs.next()) {
                            responseJson.put("hitokoto", "Error");
                        }
                        else {
                            responseJson.put("hitokoto", rs.getString(1));
                            String temp = rs.getString(2);
                            responseJson.put("from_who", temp == null ? "null" : temp);
                            temp = rs.getString(3);
                            responseJson.put("hfrom", temp == null ? "null" : temp);
                        }
                    }
                }
            }
            else {
                try(PreparedStatement ps = conn.prepareStatement("SELECT SUM(TABLE_ROWS) FROM `information_schema`.`tables` WHERE TABLE_NAME='hitokoto'")){
                    try(ResultSet rs = ps.executeQuery()){
                        if(!rs.next()) {
                            responseJson.put("maxId", -1);
                        }
                        else {
                            responseJson.put("maxId", rs.getLong(1));
                        }
                    }
                }
            }
        }
        return responseJson;
    }
}