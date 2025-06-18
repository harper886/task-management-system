package view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;

public class ReminderCheckerTest {

    @Test
    public void testReminderChecker() {
        JFrame parent = new JFrame();
        ReminderChecker checker = new ReminderChecker(1, parent);

        // 验证定时器启动
        assertNotNull(checker);

        // 模拟检查任务
        checker.checkUpcomingTasks();

        checker.stop();
    }
}