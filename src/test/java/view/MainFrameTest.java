package view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;

public class MainFrameTest {

    @Test
    public void testMainFrame() {
        MainFrame frame = new MainFrame(1);
        frame.setVisible(true);

        // 验证组件
        assertNotNull(frame.getContentPane());
        assertEquals("任务管理系统", frame.getTitle());

        // 测试按钮点击
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component button : panel.getComponents()) {
                    if (button instanceof JButton) {
                        ((JButton) button).doClick();
                    }
                }
            }
        }

        frame.dispose();
    }
}