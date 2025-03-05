<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" href="static/css/main.css">
    <link rel="stylesheet" href="static/css/error.css">
    <style>



    </style>
</head>
<body>
<div class="error-container">
    <h1>Error!</h1>
    <p><%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "An unknown error occurred." %></p>
    <a href="rooms">Back to Room Management</a>
</div>
</body>
</html>