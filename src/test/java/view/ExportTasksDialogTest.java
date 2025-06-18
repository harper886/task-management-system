package view;

import org.junit.jupiter.api.Test;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;

public class ExportTasksDialogTest {

    @Test
    public void testExportDialog() {
        ExportTasksDialog dialog = new ExportTasksDialog(1);
        dialog.setVisible(true);

        // 验证组件
        assertNotNull(dialog.getContentPane());
        assertEquals("导出任务", dialog.getTitle());

        // 测试格式选择
        JComboBox<String> combo = (JComboBox<String>) TestUtils.findComponent(dialog, JComboBox.class);
        assertNotNull(combo);
        combo.setSelectedItem("CSV");
        assertEquals("CSV", combo.getSelectedItem());

        // 测试导出按钮
        JButton exportButton = (JButton) TestUtils.findComponent(dialog, JButton.class);
        assertNotNull(exportButton);
        exportButton.doClick();

        dialog.dispose();
    }
}