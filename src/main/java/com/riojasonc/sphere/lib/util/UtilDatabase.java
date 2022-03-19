package com.riojasonc.sphere.lib.util;

import java.sql.*;
import com.riojasonc.sphere.data.License;
import com.riojasonc.sphere.data.User;
import com.riojasonc.sphere.global.CONSTANT;
import net.sf.json.JSONObject;

/*
* 请求所返回的数据前缀约定：
* \u5410 在合法的前提下，服务器是否成功返回正确数据（满足为Success，否则为为Error）
* \u5420 客户端所请求的数据是否存在等（满足为Success，否则为Error）
 */
public class UtilDatabase {
    private static final String user = "root";
    private static final String Driver = "com.mysql.cj.jdbc.Driver";

    //private static final String url = "jdbc:mysql://localhost:3306/sphere?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    //private static final String password = "MySQL5410*";
    private static final String url = "jdbc:mysql://localhost/sphere?useSSL=false&characterEncoding=utf8";
    private static final String password = "MySQL5410";

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

    public static int loginCheck(Object inputFront, String inputPassword) throws SQLException {
        boolean mod = inputFront instanceof Long; //true代表id,false代表name
        try(Connection conn = connectionGet()){
            try(PreparedStatement ps = conn.prepareStatement(mod ? "SELECT password FROM user_info WHERE id=?" : "SELECT password FROM user_info WHERE name=?")){
                ps.setObject(1, inputFront);

                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) {
                        return CONSTANT.ERROR.LOGIN.FRONT; //id或者name不存在
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
     * @param 用户名或id
     * @return Object 如果成功则返回Long型，失败则返回String型
     */
    public static Object getIdByName(String name) throws SQLException {
        try(Connection conn = connectionGet()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT id FROM user_info WHERE name=?")) {
                ps.setObject(1, name);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return rs.getLong(1);
                    }
                    else {
                        PreparedStatement ps0 = conn.prepareStatement("SELECT * FROM user_info WHERE id=?");
                        ResultSet rs0 = ps0.executeQuery();

                        return rs0.next() ? Long.parseLong(name) : CONSTANT.SERVER_RESPONSE.DATA.FAILURE;
                    }
                }
            }
        }
    } //通过用户昵称获取Id

    /*
     * @param JSONObject 需要获取的数据
     * @return JSONObject 对应的数据
     */
    public static JSONObject getInfoById(JSONObject json) throws SQLException {
        Object id = getIdByName(json.getString("id"));
        JSONObject responseJson = new JSONObject();

        if(id instanceof String) {
            responseJson.put("ResponseStatus", "Error");
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
                        responseJson.put("ResponseStatus", "Error");
                        responseJson.put("ErrorType", "InvalidParam"); //无效的参数
                        return responseJson;
                    }
                }
            }
            responseJson.put("ResponseStatus", "Success");

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

}