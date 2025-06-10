package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarView extends JFrame {
    // 定义固定颜色方案
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 255); // 主背景色
    private static final Color HEADER_BG_COLOR = new Color(80, 140, 200);   // 顶部标题栏背景
    private static final Color WEEKDAY_BG_COLOR = new Color(220, 230, 250); // 星期标题背景
    private static final Color TODAY_BG_COLOR = new Color(255, 245, 245);   // 今天背景
    private static final Color OVERDUE_BG_COLOR = new Color(255, 230, 230); // 过期任务背景
    private static final Color HIGH_PRIORITY_BG_COLOR = new Color(255, 245, 230); // 高优先级背景
    private static final Color NORMAL_TASK_BG_COLOR = new Color(235, 245, 255);   // 普通任务背景
    private static final Color NON_MONTH_BG_COLOR = new Color(245, 248, 252);     // 非当前月背景
    private static final Color BORDER_COLOR = new Color(210, 220, 230);     // 边框颜色
    private static final Color TEXT_COLOR = new Color(50, 70, 120);         // 主要文字颜色
    private static final Color SECONDARY_TEXT_COLOR = new Color(100, 100, 150); // 次要文字颜色

    private LocalDate currentDate;
    private int userId;
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private Map<LocalDate, Integer> taskCountMap = new HashMap<>();

    public CalendarView(int userId) {
        this.userId = userId;
        this.currentDate = LocalDate.now();

        setTitle("任务日历视图");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadTaskData();
        updateCalendar();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // 顶部控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 日历面板
        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBackground(BACKGROUND_COLOR);
        calendarPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JLabel legendLabel = new JLabel("图例: ");
        legendLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        legendLabel.setForeground(TEXT_COLOR);
        statusPanel.add(legendLabel);

        // 今天图例
        statusPanel.add(createLegendLabel("今天", TODAY_BG_COLOR, new Color(220, 180, 180)));

        // 过期任务图例
        statusPanel.add(createLegendLabel("已过期", OVERDUE_BG_COLOR, new Color(220, 150, 150)));

        // 高优先级图例
        statusPanel.add(createLegendLabel("高优先级", HIGH_PRIORITY_BG_COLOR, new Color(220, 180, 150)));

        // 普通任务图例
        statusPanel.add(createLegendLabel("普通任务", NORMAL_TASK_BG_COLOR, new Color(180, 200, 230)));

        return statusPanel;
    }

    private JLabel createLegendLabel(String text, Color bgColor, Color borderColor) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setBorder(new LineBorder(borderColor, 1));
        label.setPreferredSize(new Dimension(70, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(HEADER_BG_COLOR);
        controlPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // 月份标签
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        monthLabel.setForeground(Color.WHITE);
        controlPanel.add(monthLabel, BorderLayout.CENTER);

        // 导航按钮
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setOpaque(false);

        navPanel.add(createNavButton("<<", e -> currentDate = currentDate.minusYears(1)));
        navPanel.add(createNavButton("<", e -> currentDate = currentDate.minusMonths(1)));
        navPanel.add(createNavButton("今天", e -> currentDate = LocalDate.now()));
        navPanel.add(createNavButton(">", e -> currentDate = currentDate.plusMonths(1)));
        navPanel.add(createNavButton(">>", e -> currentDate = currentDate.plusYears(1)));

        controlPanel.add(navPanel, BorderLayout.EAST);

        return controlPanel;
    }

    private JButton createNavButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(new Color(60, 120, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        button.addActionListener(e -> updateCalendar());
        return button;
    }

    private void loadTaskData() {
        taskCountMap.clear();
        List<Task> tasks = TaskDAO.getTasksByUser(userId);

        for (Task task : tasks) {
            LocalDate dueDate = task.getDueDate().toLocalDate();
            taskCountMap.put(dueDate, taskCountMap.getOrDefault(dueDate, 0) + 1);
        }
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        // 更新月份标签
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
        monthLabel.setText(currentDate.format(formatter));

        // 添加星期标题
        String[] daysOfWeek = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        for (String day : daysOfWeek) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            label.setOpaque(true);
            label.setBackground(WEEKDAY_BG_COLOR);
            label.setForeground(TEXT_COLOR);
            label.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            calendarPanel.add(label);
        }

        // 获取当前月份信息
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        LocalDate lastOfMonth = yearMonth.atEndOfMonth();

        // 计算日历起始日（周日开始）
        LocalDate calendarDate = firstOfMonth.with(DayOfWeek.SUNDAY);
        if (calendarDate.isAfter(firstOfMonth)) {
            calendarDate = calendarDate.minusWeeks(1);
        }

        // 填充日历
        while (calendarDate.isBefore(lastOfMonth.plusMonths(1).withDayOfMonth(1))) {
            calendarPanel.add(createDayPanel(calendarDate));
            calendarDate = calendarDate.plusDays(1);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JPanel createDayPanel(LocalDate date) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // 日期标签
        JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
        dateLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        dateLabel.setPreferredSize(new Dimension(30, 30));

        // 任务计数标签
        int taskCount = taskCountMap.getOrDefault(date, 0);
        JLabel countLabel = new JLabel(taskCount > 0 ? taskCount + " 任务" : "", SwingConstants.CENTER);
        countLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        countLabel.setForeground(SECONDARY_TEXT_COLOR);

        // 设置背景颜色
        Color bgColor = BACKGROUND_COLOR;

        // 当前日期高亮
        if (date.equals(LocalDate.now())) {
            bgColor = TODAY_BG_COLOR;
            dateLabel.setForeground(Color.RED);
        }
        // 非当前月日期
        else if (date.getMonth() != currentDate.getMonth()) {
            bgColor = NON_MONTH_BG_COLOR;
            dateLabel.setForeground(new Color(180, 190, 200));
        }
        // 有任务的日期
        else if (taskCount > 0) {
            // 过期任务
            if (date.isBefore(LocalDate.now())) {
                bgColor = OVERDUE_BG_COLOR;
            }
            // 高优先级任务
            else if (hasHighPriorityTask(date)) {
                bgColor = HIGH_PRIORITY_BG_COLOR;
            }
            // 普通任务
            else {
                bgColor = NORMAL_TASK_BG_COLOR;
            }
        }

        panel.setBackground(bgColor);

        // 添加鼠标悬停效果
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (date.getMonth() == currentDate.getMonth()) {
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (date.getMonth() == currentDate.getMonth() && taskCount > 0) {
                    showTasksForDate(date);
                }
            }
        });

        panel.add(dateLabel, BorderLayout.NORTH);
        panel.add(countLabel, BorderLayout.CENTER);

        return panel;
    }

    private boolean hasHighPriorityTask(LocalDate date) {
        List<Task> tasks = TaskDAO.getTasksByUser(userId);
        for (Task task : tasks) {
            if (task.getDueDate().toLocalDate().equals(date) && "高".equals(task.getPriority())) {
                return true;
            }
        }
        return false;
    }

    private void showTasksForDate(LocalDate date) {
        List<Task> tasks = TaskDAO.getTasksByUser(userId);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Task task : tasks) {
            if (task.getDueDate().toLocalDate().equals(date)) {
                String statusIcon = "待办".equals(task.getStatus()) ? "◻" :
                        "进行中".equals(task.getStatus()) ? "◐" : "✓";
                String priorityColor = "高".equals(task.getPriority()) ? "<font color='#FF3333'>" :
                        "中".equals(task.getPriority()) ? "<font color='#FF9900'>" : "<font color='#33AA33'>";

                String taskInfo = String.format("<html>%s %s%s</font> - %s <small>(%s)</small></html>",
                        statusIcon, priorityColor, task.getTitle(),
                        task.getDueDate().format(timeFormatter), task.getPriority());

                listModel.addElement(taskInfo);
            }
        }

        JList<String> taskList = new JList<>(listModel);
        taskList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        taskList.setBackground(BACKGROUND_COLOR);
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(8, 15, 8, 15));
                label.setOpaque(true);

                if (isSelected) {
                    label.setBackground(new Color(220, 230, 250));
                } else {
                    label.setBackground(index % 2 == 0 ?
                            new Color(245, 248, 255) : BACKGROUND_COLOR);
                }

                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

        // 创建自定义对话框
        JDialog dialog = new JDialog(this, date.format(dateFormatter) + " 的任务", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // 添加标题
        JLabel titleLabel = new JLabel(date.format(dateFormatter) + " 的任务", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        titleLabel.setForeground(TEXT_COLOR);
        dialog.add(titleLabel, BorderLayout.NORTH);

        // 添加任务列表
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 添加关闭按钮
        JButton closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        closeButton.setBackground(new Color(80, 140, 200));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalendarView calendar = new CalendarView(1);
            calendar.setVisible(true);
        });
    }
}