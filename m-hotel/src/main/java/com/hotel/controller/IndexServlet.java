package com.hotel.controller;

import com.hotel.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // 用户未登录，重定向到登录页面
            response.sendRedirect("login.jsp");
            return;
        }

        if ("admin".equals(user.getRole())) {
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
        } else if ("user".equals(user.getRole())) {
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
        } else {
            response.sendRedirect("login.jsp");
        }
    }
}