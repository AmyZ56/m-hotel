<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hotel.model.User" %>
<%@ page import="com.hotel.dao.UserDAO" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>修改个人信息</title>
    <link rel="stylesheet" href="../../static/css/main.css">
</head>
<body>

<div class="sidebar">
    <h2>菜单</h2>
    <%
        if (user != null) {
            if ("admin".equals(user.getRole())) {
    %>
    <button onclick="location.href='${pageContext.request.contextPath}/index'">首页</button>
    <button onclick="location.href='${pageContext.request.contextPath}/rooms'">房间管理</button>
    <button onclick="location.href='${pageContext.request.contextPath}/orders'">订单管理</button>
    <%
    } else if ("user".equals(user.getRole())) {
    %>
    <button onclick="location.href='${pageContext.request.contextPath}/index'">首页</button>
    <button onclick="location.href='${pageContext.request.contextPath}/rooms'">房间预定</button>
    <button onclick="location.href='${pageContext.request.contextPath}/orders'">查看订单</button>
    <button onclick="location.href='${pageContext.request.contextPath}/update'">修改信息</button>
    <%
            }
        }
    %>
    <div class="logout-link">
        <a href="#" onclick="confirmLogout()">登出</a>
    </div>
</div>

<div class="container">
    <h1>修改个人信息</h1>
    <form action="update" method="post" onsubmit="return confirmSubmit()">
        <input type="hidden" name="username" value="${user.username}">
        <div class="form-group">
            <label>姓名：</label>
            <input type="text" name="name" value="${user.name}" required>
        </div>
        <div class="form-group">
            <label>性别：</label>
            <input type="text" name="sex" value="${user.sex}" required>
        </div>
        <div class="form-group">
            <label>电话：</label>
            <input type="text" name="phone" value="${user.phone}" required>
        </div>
        <button type="submit">保存</button>
    </form>
</div>

<script>
    function confirmLogout() {
        if (confirm("确定要登出吗？")) {
            window.location.href = "../../login.jsp";
        }
    }

    function confirmSubmit() {
        return confirm("确定要修改个人信息吗？");
    }
</script>
</body>
</html>