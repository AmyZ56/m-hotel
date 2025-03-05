package com.hotel.dao;

import com.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RoomDAO {
    private static Properties properties = UserDAO.properties;

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM rooms";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Room room = new Room();
                        room.setRoomId(rs.getInt("roomId"));
                        room.setRoomNumber(rs.getString("roomNumber"));
                        room.setRoomType(rs.getString("roomType"));
                        room.setPrice(rs.getDouble("price"));
                        room.setAvailability(rs.getBoolean("availability"));
                        room.setImagePath(rs.getString("image_path")); // 获取图片路径
                        rooms.add(room);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean isRoomBookedByUser(int roomId, int userId) {
        try (Connection conn = createConnection()) {
            String sql = "SELECT COUNT(*) FROM orders WHERE roomId = ? AND userId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, roomId);
                pstmt.setInt(2, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Room getRoomById(int roomId) {
        Room room = null;
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM rooms WHERE roomId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, roomId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        room = new Room();
                        room.setRoomId(rs.getInt("roomId"));
                        room.setRoomNumber(rs.getString("roomNumber"));
                        room.setRoomType(rs.getString("roomType"));
                        room.setPrice(rs.getDouble("price"));
                        room.setAvailability(rs.getBoolean("availability"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 检查房间是否已被预订
        if (room != null) {
            String checkBookingSql = "SELECT COUNT(*) FROM orders WHERE roomId = ? AND endDate >= ? AND startDate <= ?";
            try (Connection conn = createConnection()) {
                try (PreparedStatement pstmt = conn.prepareStatement(checkBookingSql)) {
                    pstmt.setInt(1, roomId);
                    pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis())); // 当前日期
                    pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis())); // 当前日期
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            room.setAvailability(count == 0); // 如果有预订，设置为不可用
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return room;
    }

    public boolean addRoom(Room room) {
        boolean success = false;
        try (Connection conn = createConnection()) {
            // 修改 SQL 语句，包含 image_path 字段
            String sql = "INSERT INTO rooms (roomNumber, roomType, price, availability, image_path) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, room.getRoomNumber());
                pstmt.setString(2, room.getRoomType());
                pstmt.setDouble(3, room.getPrice());
                pstmt.setBoolean(4, room.isAvailability());
                pstmt.setString(5, room.getImagePath()); // 设置图片路径
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

    public boolean updateRoom(Room room) {
        boolean success = false;
        try (Connection conn = createConnection()) {
            // 修改 SQL 语句，包含 image_path 字段
            String sql = "UPDATE rooms SET roomNumber = ?, roomType = ?, price = ?, availability = ?, image_path = ? WHERE roomId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, room.getRoomNumber());
                pstmt.setString(2, room.getRoomType());
                pstmt.setDouble(3, room.getPrice());
                pstmt.setBoolean(4, room.isAvailability());
                pstmt.setString(5, room.getImagePath()); // 设置图片路径
                pstmt.setInt(6, room.getRoomId());
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

    public boolean deleteRoom(int roomId) {
        boolean success = false;
        try (Connection conn = createConnection()) {
            String sql = "DELETE FROM rooms WHERE roomId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, roomId);
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