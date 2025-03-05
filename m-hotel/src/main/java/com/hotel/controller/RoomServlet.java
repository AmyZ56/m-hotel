package com.hotel.controller;

import com.hotel.dao.OrderDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Order;
import com.hotel.model.Room;
import com.hotel.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/rooms")
@MultipartConfig
public class RoomServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (session != null) {
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage");
            }
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
        }

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        RoomDAO roomDAO = new RoomDAO();
        List<Room> rooms = roomDAO.getAllRooms();

        // 检查每个房间是否已被当前用户预订
        for (Room room : rooms) {
            boolean isBooked = roomDAO.isRoomBookedByUser(room.getRoomId(), user.getUserId());
            room.setBookedByUser(isBooked);
        }

        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/WEB-INF/views/room.jsp").forward(request, response);
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
        RoomDAO roomDAO = new RoomDAO();
        OrderDAO orderDAO = new OrderDAO();

        try {
            if ("book".equals(action)) {
                // 用户预订逻辑
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                java.sql.Date startDate = java.sql.Date.valueOf(request.getParameter("startDate"));
                java.sql.Date endDate = java.sql.Date.valueOf(request.getParameter("endDate"));

                Order order = new Order();
                order.setUserId(user.getUserId());
                order.setRoomId(roomId);
                order.setStartDate(startDate);
                order.setEndDate(endDate);
                order.setStatus("已订");
                order.setBookingDate(new java.sql.Date(System.currentTimeMillis()));

                boolean success = orderDAO.addOrder(order);
                if (success) {
                    session.setAttribute("successMessage", "预订成功！");
                    response.sendRedirect("rooms");
                } else {
                    session.setAttribute("errorMessage", "预订失败");
                    response.sendRedirect("rooms");
                }

            } else if ("add".equals(action) && "admin".equals(user.getRole())) {
                // 处理文件上传目录
                String uploadDirPath = getServletContext().getRealPath("uploads/rooms/");
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                // 文件上传处理
                Part filePart = request.getPart("roomImage");
                String roomImage = "default.jpg";
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String filePath = "uploads/rooms/" + fileName;
                    try (InputStream fileContent = filePart.getInputStream();
                         OutputStream out = new FileOutputStream(new File(uploadDir, fileName))) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileContent.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        roomImage = filePath;
                    }
                }

                boolean availability = request.getParameter("availability") != null;
                String roomNumber = request.getParameter("roomNumber");
                String roomType = request.getParameter("roomType");
                double price = Double.parseDouble(request.getParameter("price"));

                Room room = new Room(roomNumber, roomType, price, availability);
                room.setImagePath(roomImage);

                boolean success = roomDAO.addRoom(room);
                if (success) {
                    session.setAttribute("successMessage", "房间添加成功");
                    response.sendRedirect("rooms");
                } else {
                    session.setAttribute("errorMessage", "添加房间失败");
                }

            } else if ("update".equals(action) && "admin".equals(user.getRole())) {
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                String roomNumber = request.getParameter("roomNumber");
                String roomType = request.getParameter("roomType");
                double price = Double.parseDouble(request.getParameter("price"));
                boolean availability = request.getParameter("availability") != null;

                // 文件上传处理
                Part filePart = request.getPart("roomImage");
                String imagePath = null;
                if (filePart != null && filePart.getSize() > 0) {
                    String uploadDirPath = getServletContext().getRealPath("uploads/rooms/");
                    File uploadDir = new File(uploadDirPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String filePath = "uploads/rooms/" + fileName;
                    try (InputStream fileContent = filePart.getInputStream();
                         OutputStream out = new FileOutputStream(new File(uploadDir, fileName))) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileContent.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        imagePath = filePath;
                    }
                }

                Room room = new Room();
                room.setRoomId(roomId);
                room.setRoomNumber(roomNumber);
                room.setRoomType(roomType);
                room.setPrice(price);
                room.setAvailability(availability);
                if (imagePath != null) room.setImagePath(imagePath);

                boolean success = roomDAO.updateRoom(room);
                if (success) {
                    session.setAttribute("successMessage", "房间更新成功！");
                    response.sendRedirect("rooms");
                } else {
                    session.setAttribute("errorMessage", "更新房间失败");
                }

            } else if ("delete".equals(action) && "admin".equals(user.getRole())) {
                // 删除
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                boolean success = roomDAO.deleteRoom(roomId);
                if (success) {
                    session.setAttribute("successMessage", "房间删除成功！");
                } else {
                    session.setAttribute("errorMessage", "删除房间失败");
                }
                response.sendRedirect("rooms");

            } else if ("search".equals(action)) {
                // 搜索
                String keyword = request.getParameter("keyword");
                List<Room> rooms = roomDAO.getAllRooms().stream()
                        .filter(room -> room.getRoomNumber().contains(keyword) || room.getRoomType().contains(keyword))
                        .collect(Collectors.toList());
                request.setAttribute("rooms", rooms);
                request.getRequestDispatcher("/WEB-INF/views/room.jsp").forward(request, response);
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "操作失败：" + e.getMessage());
            response.sendRedirect("rooms");
        }
    }
}