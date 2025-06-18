package view;

import org.junit.jupiter.api.Test;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;

public class RegisterFrameTest {

    @Test
    public void testRegisterFrame() {
        RegisterFrame frame = new RegisterFrame();
        frame.setVisible(true);

        // 验证组件
        assertEquals("用户注册 - 任务管理系统", frame.getTitle());

        // 测试注册按钮
        JButton registerButton = (JButton) TestUtils.findComponent(frame, "注 册");
        assertNotNull(registerButton);
        registerButton.doClick();

        frame.dispose();
    }
}