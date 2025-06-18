package view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;

public class CalendarViewTest {

    @Test
    public void testCalendarViewCreation() {
        SwingUtilities.invokeLater(() -> {
            CalendarView calendar = new CalendarView(1);
            calendar.setVisible(true);

            // 验证组件
            assertNotNull(calendar.getContentPane());
            assertTrue(calendar.isVisible());
            assertEquals("任务日历视图", calendar.getTitle());

            // 测试导航按钮
            for (Component comp : calendar.getContentPane().getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    for (Component innerComp : panel.getComponents()) {
                        if (innerComp instanceof JButton) {
                            JButton button = (JButton) innerComp;
                            button.doClick(); // 模拟点击
                        }
                    }
                }
            }

            calendar.dispose();
        });
    }
}