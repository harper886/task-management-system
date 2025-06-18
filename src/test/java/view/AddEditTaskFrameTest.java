package view;

import org.junit.jupiter.api.Test;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;

public class AddEditTaskFrameTest {

    @Test
    public void testAddTaskFrame() {
        AddEditTaskFrame frame = new AddEditTaskFrame(1, null, () -> {});
        frame.setVisible(true);

        // 验证标题
        assertEquals("添加任务", frame.getTitle());

        // 测试保存按钮
        JButton saveButton = (JButton) TestUtils.findComponent(frame, "保存");
        assertNotNull(saveButton);
        saveButton.doClick();

        frame.dispose();
    }
}