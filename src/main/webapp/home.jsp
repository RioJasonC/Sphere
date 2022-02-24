<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Sphere</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/login" method="post">
        账号：<input type="text" name="id" maxlength="24"> <br>
        密码：<input type="password" name="password" maxlength="24"> <br>
        <input type="submit" value="登录">
        <input type="button" value="注册" onclick="location.href='${pageContext.request.contextPath}/signup'">
    </form>
</body>
</html>
