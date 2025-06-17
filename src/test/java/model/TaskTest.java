package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

        // 测试全参数构造函数
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

        // 测试简化构造函数
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

        // 测试无效状态
        task.setStatus("无效状态");
        assertEquals("无效状态", task.getStatus());
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

        // 测试无效优先级
        task.setPriority("紧急");
        assertEquals("紧急", task.getPriority());
    }

    @Test
    void testToString() {
        // 准备测试数据
        LocalDateTime dueDate = LocalDateTime.of(2023, 6, 15, 14, 30);
        LocalDateTime createTime = LocalDateTime.of(2023, 6, 10, 9, 0);

        // 创建任务对象
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
        // 测试对象相等性 - 基于任务ID
        LocalDateTime now = LocalDateTime.now();

        // 相同ID的任务应被视为相等，即使其他属性不同
        Task task1 = new Task(5, "任务A", "描述A", "中", now.plusDays(5), "待办", 500);
        Task task2 = new Task(5, "任务B", "描述B", "高", now.plusDays(6), "进行中", 600);

        // 不同ID的任务应被视为不相等
        Task task3 = new Task(6, "任务A", "描述A", "中", now.plusDays(5), "待办", 500);

        // 验证相等性
        assertEquals(task1, task2, "相同ID的任务应相等");
        assertEquals(task1.hashCode(), task2.hashCode(), "相同ID的任务应有相同哈希码");

        assertNotEquals(task1, task3, "不同ID的任务应不相等");
        assertNotEquals(task1.hashCode(), task3.hashCode(), "不同ID的任务应有不同哈希码");

        // 边界情况测试
        assertNotEquals(task1, null, "任务不应等于null");
        assertNotEquals(task1, "字符串对象", "任务不应等于其他类型对象");

        // 自反性测试
        assertEquals(task1, task1, "任务应等于自身");

        // 对称性测试
        assertEquals(task2, task1, "相等性应是对称的");

        // 传递性测试
        Task task4 = new Task(5, "任务C", "描述C", "低", now.plusDays(7), "已完成", 700);
        assertEquals(task1, task4, "相同ID的任务应相等（传递性）");
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
        assertEquals(deleteTime.truncatedTo(ChronoUnit.SECONDS),
                task.getDeleteTime().truncatedTo(ChronoUnit.SECONDS));

        // 恢复任务
        task.setDeleted(false);
        task.setDeleteTime(null);

        assertFalse(task.isDeleted());
        assertNull(task.getDeleteTime());
    }

    @Test
    void testCreateTimeAutoGeneration() {
        // 测试创建时间自动生成
        Task task1 = new Task();
        assertNotNull(task1.getCreateTime(), "默认构造器应自动生成创建时间");

        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        Task task2 = new Task(7, "新任务", null, null, null, null, 0);
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        assertNotNull(task2.getCreateTime(), "简化构造器应自动生成创建时间");
        assertTrue(task2.getCreateTime().isAfter(beforeCreation) &&
                        task2.getCreateTime().isBefore(afterCreation),
                "创建时间应在合理范围内");
    }

    @Test
    void testEqualityConsistency() {
        // 测试相等性一致性
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(8, "任务", "描述", "中", now, "待办", 800);
        Task task2 = new Task(8, "任务", "描述", "中", now, "待办", 800);

        // 多次比较结果应一致
        assertEquals(task1, task2);
        assertEquals(task1, task2);

        // 修改后应不相等
        task2.setTaskId(9);
        assertNotEquals(task1, task2);
    }
}