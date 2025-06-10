package dao;

import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskDAOTest {
    private TaskDAO taskDAO;
    private int testUserId = 999; // 测试用户ID

    @BeforeEach
    void setUp() throws SQLException {
        taskDAO = new TaskDAO();
        clearTestData(); // 清空测试数据
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearTestData(); // 测试后清理数据
    }

    // 清空测试数据
    private void clearTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE user_id = ?")) {
            stmt.setInt(1, testUserId);
            stmt.executeUpdate();
        }
    }

    @Test
    void testSaveAndGetTask() throws SQLException {
        // 创建新任务
        Task task = new Task();
        task.setTitle("测试任务");
        task.setDescription("这是一个测试任务");
        task.setPriority("高");
        task.setDueDate(LocalDateTime.now().plusDays(5));
        task.setStatus("待办");
        task.setUserId(testUserId);

        // 保存任务
        boolean saveResult = taskDAO.saveTask(task);
        assertTrue(saveResult);
        assertTrue(task.getTaskId() > 0); // ID应被设置

        // 根据ID获取任务
        Task retrievedTask = taskDAO.getTaskById(task.getTaskId());
        assertNotNull(retrievedTask);
        assertEquals("测试任务", retrievedTask.getTitle());
        assertEquals("这是一个测试任务", retrievedTask.getDescription());
        assertEquals("高", retrievedTask.getPriority());
        assertEquals("待办", retrievedTask.getStatus());
        assertEquals(testUserId, retrievedTask.getUserId());
    }

    @Test
    void testUpdateTask() throws SQLException {
        // 创建任务并保存
        Task task = createTestTask();
        taskDAO.saveTask(task);

        // 修改任务
        task.setTitle("更新后的任务");
        task.setStatus("进行中");
        boolean updateResult = taskDAO.saveTask(task);
        assertTrue(updateResult);

        // 验证更新
        Task updatedTask = taskDAO.getTaskById(task.getTaskId());
        assertEquals("更新后的任务", updatedTask.getTitle());
        assertEquals("进行中", updatedTask.getStatus());
    }

    @Test
    void testSoftDeleteTask() throws SQLException {
        // 创建任务并保存
        Task task = createTestTask();
        taskDAO.saveTask(task);

        // 软删除任务
        boolean deleteResult = taskDAO.softDeleteTask(task.getTaskId());
        assertTrue(deleteResult);

        // 验证任务已被标记为删除
        Task deletedTask = taskDAO.getTaskById(task.getTaskId());
        assertTrue(deletedTask.isDeleted());
        assertNotNull(deletedTask.getDeleteTime());

        // 验证任务不在普通查询中
        List<Task> tasks = taskDAO.getTasksByUser(testUserId);
        assertTrue(tasks.stream().noneMatch(t -> t.getTaskId() == task.getTaskId()));
    }

    @Test
    void testGetTasksByUser() throws SQLException {
        // 添加多个任务
        taskDAO.saveTask(createTestTask("任务1"));
        taskDAO.saveTask(createTestTask("任务2"));
        taskDAO.saveTask(createTestTask(1000, "其他用户任务")); // 不同用户

        // 获取测试用户的任务
        List<Task> tasks = taskDAO.getTasksByUser(testUserId);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> "任务1".equals(t.getTitle())));
        assertTrue(tasks.stream().anyMatch(t -> "任务2".equals(t.getTitle())));
    }

    @Test
    void testGetDeletedTasks() throws SQLException {
        // 添加正常任务
        taskDAO.saveTask(createTestTask("正常任务"));

        // 添加并删除一个任务
        Task deletedTask = createTestTask("已删除任务");
        taskDAO.saveTask(deletedTask);
        taskDAO.softDeleteTask(deletedTask.getTaskId());

        // 获取已删除任务
        List<Task> deletedTasks = taskDAO.getDeletedTasks(testUserId);
        assertEquals(1, deletedTasks.size());
        assertEquals("已删除任务", deletedTasks.get(0).getTitle());
    }

    @Test
    void testRestoreTask() throws SQLException {
        // 创建并删除任务
        Task task = createTestTask("待恢复任务");
        taskDAO.saveTask(task);
        taskDAO.softDeleteTask(task.getTaskId());

        // 恢复任务
        boolean restoreResult = taskDAO.restoreTask(task.getTaskId());
        assertTrue(restoreResult);

        // 验证任务已恢复
        Task restoredTask = taskDAO.getTaskById(task.getTaskId());
        assertFalse(restoredTask.isDeleted());
        assertNull(restoredTask.getDeleteTime());

        // 验证任务出现在普通查询中
        List<Task> tasks = taskDAO.getTasksByUser(testUserId);
        assertTrue(tasks.stream().anyMatch(t -> t.getTaskId() == task.getTaskId()));
    }

    @Test
    void testPermanentDeleteTask() throws SQLException {
        // 创建并删除任务
        Task task = createTestTask("待永久删除");
        taskDAO.saveTask(task);
        taskDAO.softDeleteTask(task.getTaskId());

        // 永久删除任务
        boolean deleteResult = taskDAO.permanentDeleteTask(task.getTaskId());
        assertTrue(deleteResult);

        // 验证任务已删除
        Task deletedTask = taskDAO.getTaskById(task.getTaskId());
        assertNull(deletedTask);
    }

    // 辅助方法：创建测试任务
    private Task createTestTask() {
        return createTestTask("测试任务");
    }

    private Task createTestTask(String title) {
        return createTestTask(testUserId, title);
    }

    private Task createTestTask(int userId, String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("描述");
        task.setPriority("中");
        task.setDueDate(LocalDateTime.now().plusDays(3));
        task.setStatus("待办");
        task.setUserId(userId);
        return task;
    }
}