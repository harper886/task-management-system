package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrashFrame extends JFrame {
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private int userId;

    public TrashFrame(int userId) {
        this.userId = userId;
        setTitle("回收站");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton restoreButton = new JButton("恢复任务");
        JButton deletePermanentlyButton = new JButton("永久删除");
        JButton emptyTrashButton = new JButton("清空回收站");
        JButton backButton = new JButton("返回主界面");

        buttonPanel.add(restoreButton);
        buttonPanel.add(deletePermanentlyButton);
        buttonPanel.add(emptyTrashButton);
        buttonPanel.add(backButton);

        // 任务表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "标题", "原状态", "删除时间", "原截止时间"}, 0);
        tasksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tasksTable);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        refreshTasks();

        // 按钮事件
        restoreButton.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tableModel.getValueAt(selectedRow, 0);
                if (TaskDAO.restoreTask(taskId)) {
                    refreshTasks();
                    JOptionPane.showMessageDialog(this, "任务已恢复");
                } else {
                    JOptionPane.showMessageDialog(this, "恢复失败");
                }
            } else {
                JOptionPane.showMessageDialog(this, "请选择要恢复的任务");
            }
        });

        deletePermanentlyButton.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "确定永久删除此任务吗？此操作不可恢复！",
                        "确认永久删除",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (TaskDAO.deleteTaskPermanently(taskId)) {
                        refreshTasks();
                        JOptionPane.showMessageDialog(this, "任务已永久删除");
                    } else {
                        JOptionPane.showMessageDialog(this, "删除失败");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "请选择要删除的任务");
            }
        });

        emptyTrashButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定清空回收站吗？此操作将永久删除所有回收站任务！",
                    "确认清空回收站",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (TaskDAO.emptyTrash(userId)) {
                    refreshTasks();
                    JOptionPane.showMessageDialog(this, "回收站已清空");
                } else {
                    JOptionPane.showMessageDialog(this, "清空失败");
                }
            }
        });

        backButton.addActionListener(e -> dispose());
    }

    public void refreshTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = TaskDAO.getDeletedTasks(userId);

        for (Task task : tasks) {
            // 添加行数据
            tableModel.addRow(new Object[]{
                    task.getTaskId(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDeleteTime() != null ? task.getDeleteTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "未知",
                    task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
        }
    }
}