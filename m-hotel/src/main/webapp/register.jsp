<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>注册页面</title>
    <link rel="stylesheet" href="static/css/login.css">
</head>
<body>
<div class="container">
    <h1>温馨小屋</h1>
    <h2>注册</h2>
    <form action="register" method="post">
        <label for="username">用户名:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">密码:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">注册</button>
    </form>
    <div class="register-link">
        已有账号？ <a href="login.jsp">点击登录</a>
    </div>
    <div class="error-message">
        ${errorMessage}
    </div>
</div>
</body>
</html>