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

public class MainFrame extends JFrame {
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private int userId;

    public MainFrame(int userId) {
        this.userId = userId;
        setTitle("任务管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加任务");
        JButton editButton = new JButton("编辑任务");
        JButton deleteButton = new JButton("删除任务");
        JButton refreshButton = new JButton("刷新");
        JButton exportButton = new JButton("导出任务");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        // 任务表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "标题", "优先级", "截止时间", "状态"}, 0);
        tasksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tasksTable);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        refreshTasks();

        // 按钮事件
        addButton.addActionListener(e -> {
            new AddEditTaskFrame(userId, null, this::refreshTasks).setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tableModel.getValueAt(selectedRow, 0);
                Task task = TaskDAO.getTaskById(taskId);
                new AddEditTaskFrame(userId, task, this::refreshTasks).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "请选择要编辑的任务");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "确定删除此任务吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (TaskDAO.deleteTask(taskId)) {
                        refreshTasks();
                    } else {
                        JOptionPane.showMessageDialog(this, "删除失败");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "请选择要删除的任务");
            }
        });

        refreshButton.addActionListener(e -> refreshTasks());
        exportButton.addActionListener(e -> new ExportTasksDialog(userId).setVisible(true));
    }

    public void refreshTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = TaskDAO.getTasksByUser(userId);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                    task.getTaskId(),
                    task.getTitle(),
                    task.getPriority(),
                    task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    task.getStatus()
            });
        }
    }
}