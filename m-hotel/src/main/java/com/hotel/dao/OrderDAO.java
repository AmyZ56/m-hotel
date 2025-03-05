package com.hotel.dao;

import com.hotel.model.Order;
import com.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class OrderDAO {
    private static Properties properties = UserDAO.properties;

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM orders";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Order order = new Order();
                        order.setOrderId(rs.getInt("orderId"));
                        order.setUserId(rs.getInt("userId"));
                        order.setRoomId(rs.getInt("roomId"));
                        order.setStartDate(rs.getDate("startDate"));
                        order.setEndDate(rs.getDate("endDate"));
                        order.setBookingDate(rs.getDate("bookingDate"));
                        order.setStatus(rs.getString("status"));
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order getOrderById(int orderId) {
        Order order = null;
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM orders WHERE orderId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, orderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        order = new Order();
                        order.setOrderId(rs.getInt("orderId"));
                        order.setUserId(rs.getInt("userId"));
                        order.setRoomId(rs.getInt("roomId"));
                        order.setStartDate(rs.getDate("startDate"));
                        order.setEndDate(rs.getDate("endDate"));
                        order.setBookingDate(rs.getDate("bookingDate"));
                        order.setStatus(rs.getString("status"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM orders WHERE userId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Order order = new Order();
                        order.setOrderId(rs.getInt("orderId"));
                        order.setUserId(rs.getInt("userId"));
                        order.setRoomId(rs.getInt("roomId"));
                        order.setStartDate(rs.getDate("startDate"));
                        order.setEndDate(rs.getDate("endDate"));
                        order.setBookingDate(rs.getDate("bookingDate"));
                        order.setStatus(rs.getString("status"));
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean addOrder(Order order) {
        boolean success = false;
        Connection conn = null;
        try {
            conn = createConnection();
            conn.setAutoCommit(false); // 关闭自动提交

            // 插入订单
            String insertOrderSql = "INSERT INTO orders (userId, roomId, startDate, endDate, bookingDate, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql)) {
                pstmt.setInt(1, order.getUserId());
                pstmt.setInt(2, order.getRoomId());
                pstmt.setDate(3, new java.sql.Date(order.getStartDate().getTime()));
                pstmt.setDate(4, new java.sql.Date(order.getEndDate().getTime()));
                pstmt.setDate(5, new java.sql.Date(order.getBookingDate().getTime()));
                pstmt.setString(6, order.getStatus());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    success = true;
                }
            }

            // 更新房间状态为不可用
            String updateRoomSql = "UPDATE rooms SET availability = false WHERE roomId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateRoomSql)) {
                pstmt.setInt(1, order.getRoomId());
                pstmt.executeUpdate();
            }

            // 提交事务
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 恢复自动提交
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    public boolean updateOrder(Order order) {
        boolean success = false;
        try (Connection conn = createConnection()) {
            String sql = "UPDATE orders SET userId = ?, roomId = ?, startDate = ?, endDate = ?, bookingDate = ?, status = ? WHERE orderId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, order.getUserId());
                pstmt.setInt(2, order.getRoomId());
                pstmt.setDate(3, new java.sql.Date(order.getStartDate().getTime()));
                pstmt.setDate(4, new java.sql.Date(order.getEndDate().getTime()));
                pstmt.setDate(5, new java.sql.Date(order.getBookingDate().getTime()));
                pstmt.setString(6, order.getStatus());
                pstmt.setInt(7, order.getOrderId());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    success = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean deleteOrder(int orderId) {
        boolean success = false;
        Connection conn = null;
        try {
            conn = createConnection();
            conn.setAutoCommit(false); // 关闭自动提交

            // 删除订单
            String deleteSql = "DELETE FROM orders WHERE orderId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, orderId);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    success = true;
                }
            }

            // 获取被删除订单的房间ID
            String getRoomIdSql = "SELECT roomId FROM orders WHERE orderId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getRoomIdSql)) {
                pstmt.setInt(1, orderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int roomId = rs.getInt("roomId");

                        // 更新房间状态为可用
                        String updateRoomSql = "UPDATE rooms SET availability = true WHERE roomId = ?";
                        try (PreparedStatement pstmt2 = conn.prepareStatement(updateRoomSql)) {
                            pstmt2.setInt(1, roomId);
                            pstmt2.executeUpdate();
                        }
                    }
                }
            }

            // 提交事务
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 恢复自动提交
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    private Connection createConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String driverClassName = properties.getProperty("db.driverClassName");

        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, username, password);
    }
}