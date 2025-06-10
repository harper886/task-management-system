package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AddEditTaskFrame extends JFrame {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> statusCombo;
    private JSpinner dueDateSpinner;
    private Task task;
    private int userId;
    private Runnable onSuccess;

    public AddEditTaskFrame(int userId, Task task, Runnable onSuccess) {
        this.userId = userId;
        this.task = task;
        this.onSuccess = onSuccess;

        setTitle(task == null ? "添加任务" : "编辑任务");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("标题:"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("描述:"));
        descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane);

        panel.add(new JLabel("优先级:"));
        priorityCombo = new JComboBox<>(new String[]{"高", "中", "低"});
        priorityCombo.setSelectedItem("中");
        panel.add(priorityCombo);

        panel.add(new JLabel("状态:"));
        statusCombo = new JComboBox<>(new String[]{"待办", "进行中", "已完成"});
        statusCombo.setSelectedItem("待办");
        panel.add(statusCombo);

        panel.add(new JLabel("截止时间:"));
        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd HH:mm");
        dueDateSpinner.setEditor(dateEditor);
        dueDateSpinner.setValue(new Date());
        panel.add(dueDateSpinner);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new SaveButtonListener());
        panel.add(saveButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        // 如果是编辑，填充数据
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            priorityCombo.setSelectedItem(task.getPriority());
            statusCombo.setSelectedItem(task.getStatus());
            dueDateSpinner.setValue(Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
        }

        add(panel);
        setVisible(true);
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(AddEditTaskFrame.this, "标题不能为空");
                return;
            }

            String description = descriptionArea.getText();
            String priority = (String) priorityCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            Date dueDate = (Date) dueDateSpinner.getValue();
            LocalDateTime dueDateTime = LocalDateTime.ofInstant(dueDate.toInstant(), ZoneId.systemDefault());

            if (task == null) {
                task = new Task();
                task.setUserId(userId);
            }

            task.setTitle(title);
            task.setDescription(description);
            task.setPriority(priority);
            task.setStatus(status);
            task.setDueDate(dueDateTime);

            if (TaskDAO.saveTask(task)) {
                onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(AddEditTaskFrame.this, "保存失败");
            }
        }
    }
}