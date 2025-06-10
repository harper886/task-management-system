package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testDefaultConstructor() {
        // 测试默认构造函数
        Task task = new Task();

        // 验证默认值
        assertEquals(0, task.getTaskId());
        assertNull(task.getTitle());
        assertNull(task.getDescription());
        assertNull(task.getPriority());
        assertNull(task.getDueDate());
        assertNull(task.getStatus());
        assertEquals(0, task.getUserId());
        assertFalse(task.isDeleted());
        assertNull(task.getDeleteTime());
        assertNotNull(task.getCreateTime()); // 创建时间应自动设置
    }

    @Test
    void testFullConstructor() {
        // 准备测试数据
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(5);
        LocalDateTime deleteTime = now.minusDays(2);
        LocalDateTime createTime = now.minusDays(10);

        // 测试全参数构造函数 - 修正参数顺序
        Task task = new Task(1, "完成报告", "编写项目总结报告", "高",
                dueDate, "进行中", 100,
                true, deleteTime, createTime);

        // 验证所有字段值
        assertEquals(1, task.getTaskId());
        assertEquals("完成报告", task.getTitle());
        assertEquals("编写项目总结报告", task.getDescription());
        assertEquals("高", task.getPriority());
        assertEquals(dueDate, task.getDueDate());
        assertEquals("进行中", task.getStatus());
        assertEquals(100, task.getUserId());
        assertTrue(task.isDeleted());
        assertEquals(deleteTime, task.getDeleteTime());
        assertEquals(createTime, task.getCreateTime());
    }

    @Test
    void testSimplifiedConstructor() {
        // 准备测试数据
        LocalDateTime dueDate = LocalDateTime.now().plusDays(3);

        // 测试简化构造函数 - 修正参数顺序
        Task task = new Task(2, "测试任务", "这是一个测试任务", "中",
                dueDate, "待办", 200);

        // 验证字段值
        assertEquals(2, task.getTaskId());
        assertEquals("测试任务", task.getTitle());
        assertEquals("这是一个测试任务", task.getDescription());
        assertEquals("中", task.getPriority());
        assertEquals(dueDate, task.getDueDate());
        assertEquals("待办", task.getStatus());
        assertEquals(200, task.getUserId());

        // 验证默认值
        assertFalse(task.isDeleted());
        assertNull(task.getDeleteTime());
        assertNotNull(task.getCreateTime()); // 创建时间应自动设置
    }

    @Test
    void testGettersAndSetters() {
        // 创建任务对象
        Task task = new Task();

        // 准备测试数据
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(7);
        LocalDateTime deleteTime = now.minusDays(1);
        LocalDateTime createTime = now.minusDays(5);

        // 测试所有setter和getter方法
        task.setTaskId(3);
        assertEquals(3, task.getTaskId());

        task.setTitle("设置器测试");
        assertEquals("设置器测试", task.getTitle());

        task.setDescription("测试所有setter方法");
        assertEquals("测试所有setter方法", task.getDescription());

        task.setPriority("低");
        assertEquals("低", task.getPriority());

        task.setDueDate(dueDate);
        assertEquals(dueDate, task.getDueDate());

        task.setStatus("已完成");
        assertEquals("已完成", task.getStatus());

        task.setUserId(300);
        assertEquals(300, task.getUserId());

        task.setDeleted(true);
        assertTrue(task.isDeleted());

        task.setDeleteTime(deleteTime);
        assertEquals(deleteTime, task.getDeleteTime());

        task.setCreateTime(createTime);
        assertEquals(createTime, task.getCreateTime());
    }

    @Test
    void testStatusTransitions() {
        // 测试状态转换
        Task task = new Task();

        // 初始状态应为null
        assertNull(task.getStatus());

        // 设置状态
        task.setStatus("待办");
        assertEquals("待办", task.getStatus());

        // 转换到进行中
        task.setStatus("进行中");
        assertEquals("进行中", task.getStatus());

        // 转换到已完成
        task.setStatus("已完成");
        assertEquals("已完成", task.getStatus());
    }

    @Test
    void testPriorityValidation() {
        // 测试优先级验证
        Task task = new Task();

        // 设置有效优先级
        task.setPriority("高");
        assertEquals("高", task.getPriority());

        task.setPriority("中");
        assertEquals("中", task.getPriority());

        task.setPriority("低");
        assertEquals("低", task.getPriority());
    }

    @Test
    void testToString() {
        // 准备测试数据
        LocalDateTime dueDate = LocalDateTime.of(2023, 6, 15, 14, 30);
        LocalDateTime createTime = LocalDateTime.of(2023, 6, 10, 9, 0);

        // 创建任务对象 - 修正参数顺序
        Task task = new Task(4, "ToString测试", "测试toString方法", "高",
                dueDate, "进行中", 400,
                false, null, createTime);

        // 验证toString输出
        String expected = "Task{" +
                "taskId=4" +
                ", title='ToString测试'" +
                ", description='测试toString方法'" +
                ", priority='高'" +
                ", dueDate=" + dueDate +
                ", status='进行中'" +
                ", userId=400" +
                ", deleted=false" +
                ", deleteTime=null" +
                ", createTime=" + createTime +
                '}';

        assertEquals(expected, task.toString());
    }

    @Test
    void testEquality() {
        // 测试对象相等性
        LocalDateTime dueDate = LocalDateTime.now();

        // 修正参数顺序：priority在dueDate之前
        Task task1 = new Task(5, "任务A", "描述A", "中", dueDate, "待办", 500);
        Task task2 = new Task(5, "任务B", "描述B", "高", dueDate, "进行中", 600);
        Task task3 = new Task(6, "任务A", "描述A", "中", dueDate, "待办", 500);

        // 相同ID应该相等
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());

        // 不同ID应该不相等
        assertNotEquals(task1, task3);
        assertNotEquals(task1.hashCode(), task3.hashCode());

        // 与null比较
        assertNotEquals(null, task1);

        // 与不同类型对象比较
        assertNotEquals("字符串", task1);
    }

    @Test
    void testDueDateValidation() {
        // 测试截止日期验证
        Task task = new Task();

        // 设置未来日期
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        task.setDueDate(futureDate);
        assertEquals(futureDate, task.getDueDate());

        // 设置过去日期（应允许）
        LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
        task.setDueDate(pastDate);
        assertEquals(pastDate, task.getDueDate());

        // 设置null值
        task.setDueDate(null);
        assertNull(task.getDueDate());
    }

    @Test
    void testDeleteBehavior() {
        // 测试删除行为
        Task task = new Task();

        // 初始状态
        assertFalse(task.isDeleted());
        assertNull(task.getDeleteTime());

        // 删除任务
        LocalDateTime deleteTime = LocalDateTime.now();
        task.setDeleted(true);
        task.setDeleteTime(deleteTime);

        assertTrue(task.isDeleted());
        assertEquals(deleteTime, task.getDeleteTime());

        // 恢复任务
        task.setDeleted(false);
        task.setDeleteTime(null);

        assertFalse(task.isDeleted());
        assertNull(task.getDeleteTime());
    }
}