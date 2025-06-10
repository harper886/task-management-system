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
        setSize(350, 200);
        setLocationRelativeTo(null);
        setModal(true);
        setResizable(false);

        // 设置与登录界面一致的背景色
        getContentPane().setBackground(new Color(240, 248, 255)); // 浅蓝色背景

        initUI();
    }

    private void initUI() {
        // 使用网格袋布局以获得更精细的控制
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // 浅蓝色背景
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // 组件间距
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题标签 - 使用登录界面风格
        JLabel titleLabel = new JLabel("导出任务");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112)); // 深蓝色文字
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // 格式选择标签
        JLabel formatLabel = new JLabel("选择导出格式:");
        formatLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(formatLabel, gbc);

        // 格式选择下拉框
        formatCombo = new JComboBox<>(new String[]{"CSV", "TXT"});
        formatCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formatCombo.setBackground(Color.WHITE);
        formatCombo.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(formatCombo, gbc);

        // =============== 修改导出按钮样式 ===============
        JButton exportButton = createStyledButton("导出");
        exportButton.addActionListener(new ExportButtonListener());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0); // 顶部间距增大
        panel.add(exportButton, gbc);

        add(panel);
    }

    // =============== 创建统一风格的按钮 ===============
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(new Color(220, 230, 241)); // 浅蓝色背景
        button.setForeground(Color.BLACK); // 黑色文字
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 200, 230));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 80, 150), 2),
                        BorderFactory.createEmptyBorder(8, 25, 8, 25)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 230, 241));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                        BorderFactory.createEmptyBorder(8, 25, 8, 25)
                ));
            }
        });

        return button;
    }

    private class ExportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String format = (String) formatCombo.getSelectedItem();
            List<Task> tasks = TaskDAO.getTasksByUser(userId);

            if (tasks.isEmpty()) {
                JOptionPane.showMessageDialog(ExportTasksDialog.this, "没有任务可导出", "导出失败",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存导出文件");

            // 设置文件过滤器
            if ("CSV".equals(format)) {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV文件 (*.csv)", "csv"));
            } else {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
            }

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
                JOptionPane.showMessageDialog(ExportTasksDialog.this,
                        "导出成功！文件已保存至: " + filePath,
                        "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(ExportTasksDialog.this,
                        "导出失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
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