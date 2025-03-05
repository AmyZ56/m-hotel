<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>
    <link rel="stylesheet" href="static/css/login.css">
</head>
<body>
<div class="container">
    <h1>温馨小屋</h1>
    <h2>登录</h2>
    <form action="login" method="post">
        <label for="username">用户名:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">密码:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">登录</button>
    </form>
    <div class="register-link">
        还没有账号？ <a href="register.jsp">点击注册</a>
    </div>
    <div class="error-message">
        ${errorMessage}
    </div>
</div>
</body>
</html>