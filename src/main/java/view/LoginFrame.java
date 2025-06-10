package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private String captcha;

    public LoginFrame() {
        setTitle("用户登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("用户名:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("验证码:"));
        captchaField = new JTextField();
        panel.add(captchaField);

        panel.add(new JLabel());
        captchaLabel = new JLabel();
        captchaLabel.setFont(new Font("Arial", Font.BOLD, 20));
        generateCaptcha();
        captchaLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                generateCaptcha();
            }
        });
        panel.add(captchaLabel);

        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(new LoginButtonListener());
        panel.add(loginButton);

        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        panel.add(registerButton);

        add(panel);
    }

    private void generateCaptcha() {
        captcha = generateRandomString(6);
        captchaLabel.setText(captcha);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String inputCaptcha = captchaField.getText();

            if (!inputCaptcha.equalsIgnoreCase(captcha)) {
                JOptionPane.showMessageDialog(LoginFrame.this, "验证码错误");
                generateCaptcha();
                return;
            }

            User user = UserDAO.authenticate(username, password);
            if (user != null) {
                new MainFrame(user.getUserId()).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "用户名或密码错误");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
//我的软件项目管理作业
