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
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255));
        setResizable(false);

        initUI();

        // 如果是编辑，填充数据
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            priorityCombo.setSelectedItem(task.getPriority());
            statusCombo.setSelectedItem(task.getStatus());
            dueDateSpinner.setValue(Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
        }

        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel(task == null ? "添加新任务" : "编辑任务");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题字段
        JLabel titleFieldLabel = new JLabel("标题:");
        titleFieldLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleFieldLabel, gbc);

        titleField = new JTextField(20);
        titleField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        // 描述字段
        JLabel descLabel = new JLabel("描述:");
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(descLabel, gbc);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(scrollPane, gbc);

        // 优先级
        JLabel priorityLabel = new JLabel("优先级:");
        priorityLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(priorityLabel, gbc);

        priorityCombo = new JComboBox<>(new String[]{"高", "中", "低"});
        priorityCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        priorityCombo.setBackground(Color.WHITE);
        priorityCombo.setSelectedItem("中");
        priorityCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(priorityCombo, gbc);

        // 状态
        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(statusLabel, gbc);

        statusCombo = new JComboBox<>(new String[]{"待办", "进行中", "已完成"});
        statusCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusCombo.setBackground(Color.WHITE);
        statusCombo.setSelectedItem("待办");
        statusCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(statusCombo, gbc);

        // 截止时间
        JLabel dueDateLabel = new JLabel("截止时间:");
        dueDateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(dueDateLabel, gbc);

        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd HH:mm");
        dueDateSpinner.setEditor(dateEditor);
        dueDateSpinner.setValue(new Date());
        dueDateSpinner.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dueDateSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        formPanel.add(dueDateSpinner, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton saveButton = createStyledButton("保存");
        saveButton.addActionListener(new SaveButtonListener());
        buttonPanel.add(saveButton);

        JButton cancelButton = createStyledButton("取消");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));

        // 使用浅色背景和黑色文字
        button.setBackground(new Color(220, 230, 241));
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

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(AddEditTaskFrame.this, "标题不能为空", "输入错误", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(AddEditTaskFrame.this, "保存失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}