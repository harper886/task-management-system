package view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;

public class TrashFrameTest {

    @Test
    public void testTrashFrame() {
        TrashFrame frame = new TrashFrame(1);
        frame.setVisible(true);

        // 验证组件
        assertEquals("回收站", frame.getTitle());
        assertNotNull(frame.getContentPane());

        // 测试按钮点击
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                for (Component button : ((JPanel) comp).getComponents()) {
                    if (button instanceof JButton) {
                        ((JButton) button).doClick();
                    }
                }
            }
        }

        frame.dispose();
    }
}