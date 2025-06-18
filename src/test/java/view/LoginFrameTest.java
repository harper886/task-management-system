package view;

import org.junit.jupiter.api.Test;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;

public class LoginFrameTest {

    @Test
    public void testLoginFrame() {
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);

        // 验证组件
        assertEquals("用户登录 - 任务管理系统", frame.getTitle());

        // 测试登录按钮
        JButton loginButton = (JButton) TestUtils.findComponent(frame, "登 录");
        assertNotNull(loginButton);
        loginButton.doClick();

        frame.dispose();
    }
}