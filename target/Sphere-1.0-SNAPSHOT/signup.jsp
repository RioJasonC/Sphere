<%--
  Created by IntelliJ IDEA.
  User: rioja
  Date: 2/24/2022
  Time: 8:48 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Sign Up</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/signup" method="post">
        昵称：<input type="text" name="name" maxlength="24"> <br>
        密码：<input type="password" name="password" maxlength="24"> <br>
        CDKey：<input type="text" name="cdkey" minlength="19" maxlength="19"> <br>
        <input type="submit" value="注册">
        <input type="button" value="返回" onclick="location.href='${pageContext.request.contextPath}/login'">
    </form>
</body>
</html>
