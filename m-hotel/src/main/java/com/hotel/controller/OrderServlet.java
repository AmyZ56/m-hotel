package com.hotel.controller;

import com.hotel.dao.OrderDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.UserDAO;
import com.hotel.model.Order;
import com.hotel.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        OrderDAO orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();

        try {
            if ("search".equals(action)) {
                // 搜索订单
                String keyword = request.getParameter("keyword");
                List<Order> orders = orderDAO.getAllOrders().stream()
                        .filter(order -> {
                            // 支持订单 ID 和订单状态的搜索
                            try {
                                int keywordAsInt = Integer.parseInt(keyword);
                                return order.getOrderId() == keywordAsInt;
                            } catch (NumberFormatException e) {
                                return order.getStatus().contains(keyword);
                            }
                        })
                        .collect(Collectors.toList());
                request.setAttribute("orders", orders);
            } else {
                // 获取订单列表
                if ("admin".equals(user.getRole())) {
                    List<Order> orders = orderDAO.getAllOrders();
                    for (Order order : orders) {
                        User orderUser = userDAO.getUserById(order.getUserId());
                        order.setUser(orderUser);
                    }
                    request.setAttribute("orders", orders);
                }
                else {
                    List<Order> orders = orderDAO.getOrdersByUserId(user.getUserId());
                    request.setAttribute("orders", orders);
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Data retrieval error.");
        }

        request.getRequestDispatcher("/WEB-INF/views/order.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        OrderDAO orderDAO = new OrderDAO();
        RoomDAO roomDAO = new RoomDAO();

        try {
            if ("add".equals(action) && "admin".equals(user.getRole())) {
                // 管理员添加订单
                int userId = Integer.parseInt(request.getParameter("userId"));
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                Date startDate = Date.valueOf(request.getParameter("startDate"));
                Date endDate = Date.valueOf(request.getParameter("endDate"));
                String status = request.getParameter("status");

                Order order = new Order(userId, roomId, startDate, endDate, status);
                boolean success = orderDAO.addOrder(order);
                if (success) {
                    response.sendRedirect("orders");
                } else {
                    request.setAttribute("errorMessage", "订单添加失败");
                    request.getRequestDispatcher("/WEB-INF/views/order.jsp").forward(request, response);
                }
            } else if ("update".equals(action) && "admin".equals(user.getRole())) {
                // 更新订单
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                int userId = Integer.parseInt(request.getParameter("userId"));
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                Date startDate = Date.valueOf(request.getParameter("startDate"));
                Date endDate = Date.valueOf(request.getParameter("endDate"));
                String status = request.getParameter("status");

                Order order = new Order();
                order.setOrderId(orderId);
                order.setUserId(userId);
                order.setRoomId(roomId);
                order.setStartDate(startDate);
                order.setEndDate(endDate);
                order.setStatus(status);
                Order existingOrder = orderDAO.getOrderById(orderId);
                order.setBookingDate(existingOrder.getBookingDate());

                boolean success = orderDAO.updateOrder(order);
                if (success) {
                    response.sendRedirect("orders");
                } else {
                    request.setAttribute("errorMessage", "订单更新失败");
                    request.getRequestDispatcher("/WEB-INF/views/order.jsp").forward(request, response);
                }
            } else if ("delete".equals(action) && "admin".equals(user.getRole())) {
                // 管理员删除订单
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                boolean success = orderDAO.deleteOrder(orderId);
                if (success) {
                    response.sendRedirect("orders");
                } else {
                    request.setAttribute("errorMessage", "订单删除失败");
                    request.getRequestDispatcher("/WEB-INF/views/order.jsp").forward(request, response);
                }
            } else {
                throw new Exception("Invalid action or insufficient privileges.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Invalid input or database error.");
            request.getRequestDispatcher("/WEB-INF/views/order.jsp").forward(request, response);
        }
    }
}