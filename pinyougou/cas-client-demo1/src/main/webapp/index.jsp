<%--
  Created by IntelliJ IDEA.
  User: 包明天
  Date: 2020/2/23
  Time: 16:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cas-demo</title>
</head>
<body>
port:28084
<%=request.getRemoteUser()%>
<a href="http://localhost:28083/cas/logout?service=https://www.163.com">退出</a>
</body>
</html>
