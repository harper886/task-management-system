package dao;

import model.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public static boolean saveTask(Task task) {
        String sql;
        if (task.getTaskId() == 0) {
            sql = "INSERT INTO task (title, description, priority, due_date, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE task SET title = ?, description = ?, priority = ?, due_date = ?, status = ? WHERE task_id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getPriority());
            pstmt.setTimestamp(4, Timestamp.valueOf(task.getDueDate()));
            pstmt.setString(5, task.getStatus());

            if (task.getTaskId() == 0) {
                pstmt.setInt(6, task.getUserId());
            } else {
                pstmt.setInt(6, task.getTaskId());
            }

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteTask(int taskId) {
        String sql = "DELETE FROM task WHERE task_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("task_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setPriority(rs.getString("priority"));
                task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                task.setStatus(rs.getString("status"));
                task.setUserId(rs.getInt("user_id"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static Task getTaskById(int taskId) {
        String sql = "SELECT * FROM task WHERE task_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("task_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setPriority(rs.getString("priority"));
                task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                task.setStatus(rs.getString("status"));
                task.setUserId(rs.getInt("user_id"));
                return task;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}