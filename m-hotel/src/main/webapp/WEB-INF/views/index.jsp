<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hotel.model.User" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>首页</title>
    <link rel="stylesheet" href="../../static/css/main.css">
    <link rel="stylesheet" href="../../static/css/index.css">
</head>
    <script>
        function confirmLogout() {
            // 弹出确认框
            if (confirm("确定要登出吗？")) {
                // 如果点击"确定"，跳转到登录页面
                window.location.href = "../../login.jsp";
            } else {
                // 如果点击"取消"，留在当前页面
                return false;
            }
        }

        // 动态显示当前时间
        function updateTime() {
            const currentTimeElement = document.getElementById("current-time");
            const now = new Date();
            currentTimeElement.innerHTML = now.toLocaleString();
        }

        // 每秒更新时间
        setInterval(updateTime, 1000);
    </script>
</head>
<body>

<div class="sidebar">
    <h2>菜单</h2>
    <%
        User user = (User) session.getAttribute("user");
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

<div class="content">
    <%
        if (user != null) {
            if ("admin".equals(user.getRole())) {
    %>
    <h1>欢迎, ${user.username}</h1>
    <%
    } else if ("user".equals(user.getRole())) {
    %>
    <h1>欢迎来到温馨小屋</h1>
    <h2>我们致力于打造一个安全、舒适的居住环境</h2>
    <%
            }
        }
    %>

    <div id="current-time" class="time"></div>
</div>
<div class="background-pattern">
    <img src="background.jpg" alt="背景图案">
</div>
</body>
</html>
