package com.hotel.dao;

import com.hotel.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class UserDAO {
    static Properties properties = new Properties();

    static {
        try {
            InputStream inputStream = UserDAO.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUserById(int userId) {
        User user = null;
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM user WHERE userId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        user = new User();
                        user.setUserId(rs.getInt("userId"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setRole(rs.getString("role"));
                        user.setCreatedAt(rs.getString("createdAt"));
                        user.setName(rs.getString("name"));
                        user.setSex(rs.getString("sex"));
                        user.setPhone(rs.getString("phone"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        User user = null;
        try (Connection conn = createConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        user = new User();
                        user.setUserId(rs.getInt("userId"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setRole(rs.getString("role"));
                        user.setCreatedAt(rs.getString("createdAt"));
                        user.setName(rs.getString("name"));
                        user.setSex(rs.getString("sex"));
                        user.setPhone(rs.getString("phone"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean insertUser(User user) {
        boolean success = false;
        try (Connection conn = createConnection()) {
            String sql = "INSERT INTO user (username, password, role, name, sex, phone) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getRole());
                pstmt.setString(4, user.getName());
                pstmt.setString(5, user.getSex());
                pstmt.setString(6, user.getPhone());
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

    // 新增：更新用户信息
    public boolean updateUser(User user) {
        boolean success = false;
        try (Connection conn = createConnection()) {
            String sql = "UPDATE user SET name = ?, sex = ?, phone = ? WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getSex());
                pstmt.setString(3, user.getPhone());
                pstmt.setString(4, user.getUsername());
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