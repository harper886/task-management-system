package dao;

import model.User;

import java.sql.*;

public class UserDAO {

    public static User authenticate(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public static boolean registerUser(String username, String password, String email) {
        // 输入验证：检查空值或空白字符串
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            System.out.println("Registration failed: Empty fields");
            return false;
        }

        // 去除前后空格
        username = username.trim();
        password = password.trim();
        email = email.trim();

        String sql = "INSERT INTO user (username, password, email, role) VALUES (?, ?, ?, 'user')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User registered: " + username);
                return true;
            }
            return false;
        } catch (SQLException e) {
            // 处理唯一约束冲突
            if (e instanceof SQLIntegrityConstraintViolationException) {
                System.out.println("Registration failed: " + e.getMessage());
            } else {
                System.err.println("Database error: " + e.getMessage());
            }
            return false;
        }
    }
}