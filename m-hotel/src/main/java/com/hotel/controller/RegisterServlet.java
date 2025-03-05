package com.hotel.controller;

import com.hotel.dao.UserDAO;
import com.hotel.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = "user"; // 默认角色为 user

        User user = new User(username, password, role);
        UserDAO userDAO = new UserDAO();

        boolean success = userDAO.insertUser(user);
        if (success) {
            request.setAttribute("message", "注册成功！请登录");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "注册失败！用户名已经存在");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}