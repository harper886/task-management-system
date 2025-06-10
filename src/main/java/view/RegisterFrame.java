package view;

import dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;

    public RegisterFrame() {
        setTitle("用户注册");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("用户名:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("邮箱:"));
        emailField = new JTextField();
        panel.add(emailField);

        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new RegisterButtonListener());
        panel.add(registerButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        panel.add(cancelButton);

        add(panel);
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();

            if (username.length() < 5 || username.length() > 20) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "用户名长度必须在5-20个字符之间");
                return;
            }

            if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "密码必须至少8个字符，包含字母和数字");
                return;
            }

            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "邮箱格式不正确");
                return;
            }

            if (UserDAO.registerUser(username, password, email)) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "注册成功！");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(RegisterFrame.this, "注册失败，用户名可能已存在");
            }
        }
    }
}