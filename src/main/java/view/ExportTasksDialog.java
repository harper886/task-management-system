package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportTasksDialog extends JDialog {
    private JComboBox<String> formatCombo;
    private int userId;

    public ExportTasksDialog(int userId) {
        this.userId = userId;
        setTitle("导出任务");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setModal(true);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("选择导出格式:"));
        formatCombo = new JComboBox<>(new String[]{"CSV", "TXT"});
        panel.add(formatCombo);

        JButton exportButton = new JButton("导出");
        exportButton.addActionListener(new ExportButtonListener());
        panel.add(exportButton);

        add(panel);
    }

    private class ExportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String format = (String) formatCombo.getSelectedItem();
            List<Task> tasks = TaskDAO.getTasksByUser(userId);

            if (tasks.isEmpty()) {
                JOptionPane.showMessageDialog(ExportTasksDialog.this, "没有任务可导出");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存导出文件");

            if (fileChooser.showSaveDialog(ExportTasksDialog.this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith("." + format.toLowerCase())) {
                filePath += "." + format.toLowerCase();
            }

            try {
                if ("CSV".equals(format)) {
                    exportToCSV(tasks, filePath);
                } else {
                    exportToTXT(tasks, filePath);
                }
                JOptionPane.showMessageDialog(ExportTasksDialog.this, "导出成功！文件已保存至: " + filePath);
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(ExportTasksDialog.this, "导出失败: " + ex.getMessage());
            }
        }

        private void exportToCSV(List<Task> tasks, String filePath) throws IOException {
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("任务ID,标题,描述,优先级,截止时间,状态\n");
                for (Task task : tasks) {
                    writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                            task.getTaskId(),
                            escapeCsvField(task.getTitle()),
                            escapeCsvField(task.getDescription()),
                            escapeCsvField(task.getPriority()),
                            task.getDueDate().toString(),
                            escapeCsvField(task.getStatus())));
                }
            }
        }

        private void exportToTXT(List<Task> tasks, String filePath) throws IOException {
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("=============== 任务列表 ===============\n\n");
                for (Task task : tasks) {
                    writer.write(String.format("任务ID: %d\n标题: %s\n描述: %s\n优先级: %s\n截止时间: %s\n状态: %s\n\n",
                            task.getTaskId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getPriority(),
                            task.getDueDate().toString(),
                            task.getStatus()));
                }
                writer.write("================ 结束 =================");
            }
        }

        // CSV字段转义方法
        private String escapeCsvField(String field) {
            if (field == null) return "";
            return field.replace("\"", "\"\"");
        }
    }
}