package dao;

import model.Task;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static dao.DatabaseConnection.getConnection;

public class TaskDAO {
    // 保存或更新任务
    public static boolean saveTask(Task task) {
        String sql;
        if (task.getTaskId() == 0) {
            sql = "INSERT INTO tasks (title, description, priority, due_date, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tasks SET title = ?, description = ?, priority = ?, due_date = ?, status = ? WHERE task_id = ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

            int affectedRows = pstmt.executeUpdate();

            // 获取自增ID
            if (task.getTaskId() == 0 && affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setTaskId(rs.getInt(1));
                    }
                }
            }

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 软删除任务（移至回收站）
    public static boolean softDeleteTask(int taskId) {
        String sql = "UPDATE tasks SET deleted = 1, delete_time = CURRENT_TIMESTAMP WHERE task_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 恢复任务（从回收站还原）
    public static boolean restoreTask(int taskId) {
        String sql = "UPDATE tasks SET deleted = 0, delete_time = NULL WHERE task_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 永久删除任务（从回收站删除）
    public static boolean deleteTaskPermanently(int taskId) {
        String sql = "DELETE FROM tasks WHERE task_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 清空回收站
    public static boolean emptyTrash(int userId) {
        String sql = "DELETE FROM tasks WHERE user_id = ? AND deleted = 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows >= 0; // 即使没有删除行也返回true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取用户所有任务（不包括已删除的）
    public static List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND deleted = 0";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(createTaskFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // 获取回收站中的任务
    public static List<Task> getDeletedTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND deleted = 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(createTaskFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // 根据ID获取任务
    public static Task getTaskById(int taskId) {
        String sql = "SELECT * FROM tasks WHERE task_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createTaskFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 工具方法：从ResultSet创建Task对象
    private static Task createTaskFromResultSet(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getInt("task_id"));
        task.setUserId(rs.getInt("user_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("status"));
        task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());

        // 处理可能为null的字段
        if (rs.getTimestamp("delete_time") != null) {
            task.setDeleteTime(rs.getTimestamp("delete_time").toLocalDateTime());
        }
        if (rs.getTimestamp("create_time") != null) {
            task.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        }

        task.setDeleted(rs.getBoolean("deleted"));
        return task;
    }
    public boolean permanentDeleteTask(int taskId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE task_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}