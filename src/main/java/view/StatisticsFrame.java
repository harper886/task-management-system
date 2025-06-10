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
import java.util.List;
import java.util.*;

public class StatisticsFrame extends JFrame {
    private int userId;

    public StatisticsFrame(int userId) {
        this.userId = userId;
        setTitle("任务统计图表");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // 状态分布饼图
        tabbedPane.addTab("状态分布", createStatusChartPanel());

        // 优先级分布饼图
        tabbedPane.addTab("优先级分布", createPriorityChartPanel());

        // 时间分布饼图
        tabbedPane.addTab("时间分布", createTimeDistributionPanel());

        add(tabbedPane);
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
                "任务状态分布",   // 图表标题
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
        plot.setLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(700, 500));

        return chartPanel;
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
                "任务优先级分布",
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
        plot.setLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

        return new ChartPanel(chart);
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
                "任务时间分布",
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
        plot.setLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

        return new ChartPanel(chart);
    }

    public static void main(String[] args) {
        // 测试数据
        SwingUtilities.invokeLater(() -> {
            StatisticsFrame frame = new StatisticsFrame(1);
            frame.setVisible(true);
        });
    }
}