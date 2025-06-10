package view;

import dao.TaskDAO;
import model.Task;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class StatisticsFrame extends JFrame {
    private int userId;

    public StatisticsFrame(int userId) {
        this.userId = userId;
        setTitle("任务统计图表");
        setSize(900, 700); // 增加尺寸以提供更好的显示
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255)); // 设置背景色
        setResizable(false); // 禁止调整大小

        initUI();
    }

    private void initUI() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        // 标题
        JLabel titleLabel = new JLabel("任务统计图表", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112)); // 深蓝色
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 标签页面板
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(240, 248, 255));
        tabbedPane.setForeground(Color.BLACK);

        // 状态分布饼图
        tabbedPane.addTab("状态分布", createStatusChartPanel());

        // 优先级分布饼图
        tabbedPane.addTab("优先级分布", createPriorityChartPanel());

        // 时间分布饼图
        tabbedPane.addTab("时间分布", createTimeDistributionPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // 关闭按钮
        JButton closeButton = createStyledButton("关闭");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(closeButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(new Color(220, 230, 241)); // 浅蓝色背景
        button.setForeground(Color.BLACK); // 黑色文字
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 200, 230));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 80, 150), 2),
                        BorderFactory.createEmptyBorder(10, 30, 10, 30)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 230, 241));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                        BorderFactory.createEmptyBorder(10, 30, 10, 30)
                ));
            }
        });

        return button;
    }

    private JPanel createStatusChartPanel() {
        // 获取任务数据
        List<Task> tasks = TaskDAO.getTasksByUser(userId);

        // 统计任务状态
        Map<String, Integer> statusCount = new HashMap<>();
        for (Task task : tasks) {
            String status = task.getStatus();
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
        }

        // 创建数据集
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                null,   // 移除图表标题（因为我们在面板上有标题）
                dataset,        // 数据集
                true,           // 是否显示图例
                true,           // 是否显示工具提示
                false           // 是否生成URL
        );

        // 自定义图表外观
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("待办", new Color(65, 105, 225));   // 蓝色
        plot.setSectionPaint("进行中", new Color(255, 165, 0));  // 橙色
        plot.setSectionPaint("已完成", new Color(50, 205, 50));  // 绿色
        plot.setBackgroundPaint(new Color(245, 248, 255));      // 背景色
        plot.setOutlineVisible(false);                          // 移除边框
        plot.setLabelBackgroundPaint(new Color(240, 248, 255)); // 标签背景色
        plot.setLabelOutlinePaint(null);                        // 移除标签边框
        plot.setLabelShadowPaint(null);                         // 移除标签阴影

        // 设置字体
        chart.getTitle().setFont(new Font("微软雅黑", Font.BOLD, 18));
        plot.setLabelFont(new Font("微软雅黑", Font.BOLD, 14)); // 增大标签字体

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        // 创建带标题的面板
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel chartTitle = new JLabel("任务状态分布", SwingConstants.CENTER);
        chartTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        chartTitle.setForeground(new Color(25, 25, 112));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPriorityChartPanel() {
        List<Task> tasks = TaskDAO.getTasksByUser(userId);

        // 统计任务优先级
        Map<String, Integer> priorityCount = new HashMap<>();
        for (Task task : tasks) {
            String priority = task.getPriority();
            priorityCount.put(priority, priorityCount.getOrDefault(priority, 0) + 1);
        }

        // 创建数据集
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : priorityCount.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false
        );

        // 自定义图表外观
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("高", new Color(220, 20, 60));     // 红色
        plot.setSectionPaint("中", new Color(255, 215, 0));     // 金色
        plot.setSectionPaint("低", new Color(50, 205, 50));     // 绿色
        plot.setBackgroundPaint(new Color(245, 248, 255));
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(new Color(240, 248, 255));
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);

        // 设置字体
        chart.getTitle().setFont(new Font("微软雅黑", Font.BOLD, 18));
        plot.setLabelFont(new Font("微软雅黑", Font.BOLD, 14)); // 增大标签字体

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        // 创建带标题的面板
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel chartTitle = new JLabel("任务优先级分布", SwingConstants.CENTER);
        chartTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        chartTitle.setForeground(new Color(25, 25, 112));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTimeDistributionPanel() {
        List<Task> tasks = TaskDAO.getTasksByUser(userId);

        // 统计任务时间分布（按时间段）
        Map<String, Integer> timeDistribution = new LinkedHashMap<>();
        timeDistribution.put("今天", 0);
        timeDistribution.put("本周", 0);
        timeDistribution.put("本月", 0);
        timeDistribution.put("未来", 0);
        timeDistribution.put("已过期", 0);

        LocalDateTime now = LocalDateTime.now();
        for (Task task : tasks) {
            if ("已完成".equals(task.getStatus())) continue;

            LocalDateTime dueDate = task.getDueDate();
            if (dueDate.isBefore(now)) {
                timeDistribution.put("已过期", timeDistribution.get("已过期") + 1);
            } else if (dueDate.toLocalDate().equals(now.toLocalDate())) {
                timeDistribution.put("今天", timeDistribution.get("今天") + 1);
            } else if (dueDate.toLocalDate().isBefore(now.toLocalDate().plusWeeks(1))) {
                timeDistribution.put("本周", timeDistribution.get("本周") + 1);
            } else if (dueDate.toLocalDate().isBefore(now.toLocalDate().plusMonths(1))) {
                timeDistribution.put("本月", timeDistribution.get("本月") + 1);
            } else {
                timeDistribution.put("未来", timeDistribution.get("未来") + 1);
            }
        }

        // 创建数据集
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : timeDistribution.entrySet()) {
            if (entry.getValue() > 0) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false
        );

        // 自定义图表外观
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("今天", new Color(255, 99, 71));      // 番茄红
        plot.setSectionPaint("本周", new Color(65, 105, 225));     // 蓝色
        plot.setSectionPaint("本月", new Color(106, 90, 205));     // 紫罗兰
        plot.setSectionPaint("未来", new Color(50, 205, 50));      // 绿色
        plot.setSectionPaint("已过期", new Color(169, 169, 169));  // 灰色
        plot.setBackgroundPaint(new Color(245, 248, 255));
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(new Color(240, 248, 255));
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);

        // 设置字体
        chart.getTitle().setFont(new Font("微软雅黑", Font.BOLD, 18));
        plot.setLabelFont(new Font("微软雅黑", Font.BOLD, 14)); // 增大标签字体

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        // 创建带标题的面板
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel chartTitle = new JLabel("任务时间分布", SwingConstants.CENTER);
        chartTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        chartTitle.setForeground(new Color(25, 25, 112));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        // 测试数据
        SwingUtilities.invokeLater(() -> {
            StatisticsFrame frame = new StatisticsFrame(1);
            frame.setVisible(true);
        });
    }
}