package view;

import dao.UserDAO;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private String captcha;
    private JButton registerButton;
    private JButton cancelButton;

    // 定义颜色方案
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color SECONDARY_COLOR = new Color(41, 182, 246);
    private final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color PLACEHOLDER_COLOR = new Color(160, 160, 160);

    public RegisterFrame() {
        setTitle("用户注册 - 任务管理系统");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建主面板
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(BACKGROUND_COLOR);

        // 创建卡片式内容面板
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制圆角卡片背景
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // 添加轻微阴影
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 添加标题
        JLabel titleLabel = new JLabel("用户注册");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        cardPanel.add(titleLabel);

        // 用户名输入区域
        JPanel usernamePanel = createInputPanel("用户名", "请输入用户名", false);
        usernameField = (JTextField) ((JPanel)usernamePanel.getComponent(1)).getComponent(0);
        cardPanel.add(usernamePanel);

        // 添加间距
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 密码输入区域
        JPanel passwordPanel = createInputPanel("密码", "请输入密码", true);
        passwordField = (JPasswordField) ((JPanel)passwordPanel.getComponent(1)).getComponent(0);
        cardPanel.add(passwordPanel);

        // 添加间距
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 邮箱输入区域
        JPanel emailPanel = createInputPanel("邮箱", "请输入邮箱", false);
        emailField = (JTextField) ((JPanel)emailPanel.getComponent(1)).getComponent(0);
        cardPanel.add(emailPanel);

        // 添加间距
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 验证码区域
        JPanel captchaPanel = new JPanel();
        captchaPanel.setLayout(new BoxLayout(captchaPanel, BoxLayout.X_AXIS));
        captchaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 验证码标签（纯文本）
        JLabel captchaIcon = new JLabel("验证码:");
        captchaIcon.setFont(new Font("微软雅黑", Font.BOLD, 16));
        captchaIcon.setForeground(PRIMARY_COLOR);
        captchaIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        captchaPanel.add(captchaIcon);

        // 验证码输入框
        JTextFieldField captchaInput = new JTextFieldField("请输入验证码");
        captchaField = captchaInput;
        captchaInput.setMaximumSize(new Dimension(180, 40));
        captchaPanel.add(captchaInput);

        // 添加间距
        captchaPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        // 验证码显示标签
        captchaLabel = new JLabel();
        captchaLabel.setOpaque(true);
        captchaLabel.setBackground(new Color(240, 240, 240));
        captchaLabel.setForeground(PRIMARY_COLOR);
        captchaLabel.setFont(new Font("Arial", Font.BOLD, 20));
        captchaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        captchaLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 15, 5, 15)
        ));
        captchaLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        captchaLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                generateCaptcha();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                captchaLabel.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                captchaLabel.setBackground(new Color(240, 240, 240));
            }
        });
        generateCaptcha();

        captchaPanel.add(captchaLabel);
        cardPanel.add(captchaPanel);

        // 添加间距
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 注册按钮
        registerButton = new GradientButton("注 册", PRIMARY_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(new RegisterButtonListener());
        registerButton.setMaximumSize(new Dimension(280, 45));
        cardPanel.add(registerButton);

        // 添加间距
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 取消按钮
        cancelButton = new GradientButton("取 消", new Color(120, 120, 120));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setMaximumSize(new Dimension(280, 45));
        cancelButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        cardPanel.add(cancelButton);

        // 添加底部版权信息
        JLabel copyrightLabel = new JLabel("© 2025 任务管理系统 - 软件项目管理作业");
        copyrightLabel.setForeground(new Color(100, 100, 100));
        copyrightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        copyrightLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 包装卡片面板
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setBorder(new EmptyBorder(30, 40, 30, 40));
        cardWrapper.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        cardWrapper.add(cardPanel, gbc);

        // 添加组件到主界面
        backgroundPanel.add(cardWrapper, BorderLayout.CENTER);
        backgroundPanel.add(copyrightLabel, BorderLayout.SOUTH);

        add(backgroundPanel);
    }

    // 创建输入框面板的通用方法
    private JPanel createInputPanel(String label, String placeholder, boolean isPassword) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);

        // 标签
        JLabel iconLabel = new JLabel(label + ":");
        iconLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        iconLabel.setForeground(PRIMARY_COLOR);
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
        panel.add(iconLabel);

        // 输入区域容器
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setOpaque(false);

        // 输入框
        if (isPassword) {
            PasswordFieldField field = new PasswordFieldField(placeholder);
            inputPanel.add(field, BorderLayout.CENTER);
        } else {
            JTextFieldField field = new JTextFieldField(placeholder);
            inputPanel.add(field, BorderLayout.CENTER);
        }

        // 添加下划线
        JPanel underlinePanel = new JPanel();
        underlinePanel.setBackground(new Color(200, 200, 200));
        underlinePanel.setPreferredSize(new Dimension(0, 1));
        inputPanel.add(underlinePanel, BorderLayout.SOUTH);

        panel.add(inputPanel);
        return panel;
    }

    // 自定义文本框
    class JTextFieldField extends JTextField {
        private String placeholder;

        public JTextFieldField(String placeholder) {
            super(20);
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setFont(new Font("微软雅黑", Font.PLAIN, 16));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(PLACEHOLDER_COLOR);
                    }
                }
            });

            // 初始状态
            if (getText().isEmpty()) {
                setText(placeholder);
                setForeground(PLACEHOLDER_COLOR);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 如果文本为空且没有焦点，绘制占位符
            if (getText().isEmpty() && !hasFocus()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(PLACEHOLDER_COLOR);
                g2d.setFont(getFont().deriveFont(Font.ITALIC));

                // 设置渲染提示
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 绘制占位文本
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top + 3);
                g2d.dispose();
            }
        }
    }

    // 自定义密码框
    class PasswordFieldField extends JPasswordField {
        private String placeholder;

        public PasswordFieldField(String placeholder) {
            super(20);
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setFont(new Font("微软雅黑", Font.PLAIN, 16));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (String.valueOf(getPassword()).equals(placeholder)) {
                        setText("");
                        setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        setForeground(PLACEHOLDER_COLOR);
                    }
                }
            });

            // 初始状态
            if (getPassword().length == 0) {
                setForeground(PLACEHOLDER_COLOR);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 如果密码为空且没有焦点，绘制占位符
            if (getPassword().length == 0 && !hasFocus()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(PLACEHOLDER_COLOR);
                g2d.setFont(getFont().deriveFont(Font.ITALIC));

                // 设置渲染提示
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 绘制占位文本
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top + 3);
                g2d.dispose();
            }
        }
    }

    // 自定义渐变按钮
    class GradientButton extends JButton {
        private Color baseColor;

        public GradientButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color startColor = baseColor;
            Color endColor = baseColor.darker();

            if (getModel().isPressed()) {
                // 按钮按下效果
                endColor = startColor;
                startColor = startColor.darker();
            } else if (getModel().isRollover()) {
                // 悬停效果
                endColor = startColor.brighter();
            }

            // 创建渐变
            GradientPaint gp = new GradientPaint(
                    0, 0, startColor,
                    0, getHeight(), endColor
            );

            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            super.paintComponent(g2d);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // 不绘制默认边框
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 45);
        }
    }

    // 生成验证码
    private void generateCaptcha() {
        captcha = generateRandomString(5);
        captchaLabel.setText(captcha);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String inputCaptcha = captchaField.getText();

            // 检查输入是否有效
            boolean isUsernameEmpty = username.isEmpty() || username.equals("请输入用户名");
            boolean isPasswordEmpty = password.isEmpty() || password.equals("请输入密码");
            boolean isEmailEmpty = email.isEmpty() || email.equals("请输入邮箱");
            boolean isCaptchaEmpty = inputCaptcha.isEmpty() || inputCaptcha.equals("请输入验证码");

            if (isUsernameEmpty || isPasswordEmpty || isEmailEmpty || isCaptchaEmpty) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "请填写完整注册信息", "信息不完整", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!inputCaptcha.equalsIgnoreCase(captcha)) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "验证码错误，请重新输入", "验证码错误", JOptionPane.ERROR_MESSAGE);
                generateCaptcha();
                return;
            }

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