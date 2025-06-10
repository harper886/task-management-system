package view;

import dao.TaskDAO;
import model.Task;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import javax.swing.table.TableModel;

public class MainFrame extends JFrame {
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<TableModel> sorter;
    private int userId;

    // 新增实时面板相关组件
    private JPanel realtimePanel;
    private JLabel timeLabel;
    private JLabel taskStats;
    private JLabel systemStatus;

    public MainFrame(int userId) {
        this.userId = userId;
        setTitle("任务管理系统");
        setSize(1200, 800); // 增加尺寸以提供更多空间
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 248, 255)); // 设置背景色

        initUI();
        refreshTasks();
        startRealtimeUpdates();
    }

    private void initUI() {
        // 主面板使用边框布局
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        // =============== 顶部按钮面板 ===============
        JPanel buttonPanel = createButtonPanel();

        // =============== 实时信息面板 ===============
        createRealtimePanel();

        // 创建北部容器，包含按钮面板和实时面板
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(buttonPanel, BorderLayout.NORTH);
        northContainer.add(realtimePanel, BorderLayout.SOUTH);
        northContainer.setBackground(new Color(240, 248, 255));

        mainPanel.add(northContainer, BorderLayout.NORTH);

        // =============== 任务表格 ===============
        JPanel tablePanel = createTablePanel();
        mainPanel.add(new JScrollPane(tablePanel), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createButtonPanel() {
        // 使用流式布局，允许按钮换行
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("任务操作"));

        // 创建按钮并应用统一样式
        JButton addButton = createStyledButton("添加任务");
        JButton editButton = createStyledButton("编辑任务");
        JButton deleteButton = createStyledButton("删除任务");
        JButton refreshButton = createStyledButton("刷新");
        JButton exportButton = createStyledButton("导出任务");
        JButton trashButton = createStyledButton("回收站");
        JButton sortByDueDateButton = createStyledButton("按截止时间排序");
        JButton sortByStatusButton = createStyledButton("按状态排序");
        JButton calendarButton = createStyledButton("日历视图");
        JButton statsButton = createStyledButton("统计图表");

        // 添加按钮到面板
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(trashButton);
        buttonPanel.add(sortByDueDateButton);
        buttonPanel.add(sortByStatusButton);
        buttonPanel.add(calendarButton);
        buttonPanel.add(statsButton);

        // 按钮事件
        addButton.addActionListener(e -> {
            new AddEditTaskFrame(userId, null, this::refreshTasks).setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = tasksTable.convertRowIndexToModel(selectedRow);
                int taskId = (int) tableModel.getValueAt(modelRow, 0);
                Task task = TaskDAO.getTaskById(taskId);
                new AddEditTaskFrame(userId, task, this::refreshTasks).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "请选择要编辑的任务");
            }
        });

        // 添加回收站按钮事件
        trashButton.addActionListener(e -> {
            new TrashFrame(userId).setVisible(true);
        });

        // 修改删除按钮事件（改为软删除）
        deleteButton.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = tasksTable.convertRowIndexToModel(selectedRow);
                int taskId = (int) tableModel.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "确定删除此任务吗？任务将移至回收站",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (TaskDAO.softDeleteTask(taskId)) {
                        refreshTasks();
                        JOptionPane.showMessageDialog(this, "任务已移至回收站");
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

        // 新增排序按钮事件
        sortByDueDateButton.addActionListener(e -> {
            // 按截止时间升序排序
            sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(3, SortOrder.ASCENDING)));
            sorter.sort();
        });

        sortByStatusButton.addActionListener(e -> {
            // 按状态排序（自定义顺序）
            sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(4, SortOrder.ASCENDING)));
            sorter.sort();
        });

        // 添加日历视图按钮事件
        calendarButton.addActionListener(e -> {
            new CalendarView(userId).setVisible(true);
        });

        // 添加统计图表按钮事件
        statsButton.addActionListener(e -> {
            new StatisticsFrame(userId).setVisible(true);
        });

        return buttonPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(new Color(220, 230, 241)); // 浅蓝色背景
        button.setForeground(Color.BLACK); // 黑色文字
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 200, 230));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 80, 150), 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 230, 241));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));

        // 任务表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "标题", "优先级", "截止时间", "状态"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // 使ID列可以正确排序
                if (columnIndex == 0) return Integer.class;
                return Object.class;
            }
        };
        tasksTable = new JTable(tableModel);

        // =============== 增大表格字体 ===============
        tasksTable.setFont(new Font("微软雅黑", Font.PLAIN, 16)); // 从14增大到16
        tasksTable.setRowHeight(35); // 从30增大到35
        tasksTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 16)); // 从14增大到16

        // 创建排序器并设置
        sorter = new TableRowSorter<>(tableModel);
        tasksTable.setRowSorter(sorter);

        // 设置截止时间列的排序器
        sorter.setComparator(3, new DueDateComparator());

        // 设置状态列的排序器
        sorter.setComparator(4, new StatusComparator());

        // 设置表格渲染器
        tasksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 设置交替行颜色
                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 250, 255)); // 浅蓝色
                } else {
                    c.setBackground(Color.WHITE);
                }

                // 设置选中行颜色
                if (isSelected) {
                    c.setBackground(new Color(200, 220, 255)); // 深蓝色
                }

                // 设置优先级颜色
                if (column == 2) {
                    String priority = (String) value;
                    if ("高".equals(priority)) {
                        c.setForeground(Color.RED);
                    } else if ("中".equals(priority)) {
                        c.setForeground(new Color(255, 140, 0)); // 橙色
                    } else {
                        c.setForeground(new Color(0, 128, 0)); // 绿色
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("任务列表"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // =============== 新增方法：创建实时面板 ===============
    private void createRealtimePanel() {
        realtimePanel = new JPanel(new GridLayout(1, 3, 10, 10));
        realtimePanel.setBorder(BorderFactory.createTitledBorder("实时信息"));
        realtimePanel.setBackground(new Color(230, 240, 255));  // 设置浅蓝色背景

        // 系统时间显示
        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 从14增大到16
        updateTime(); // 初始更新时间

        // 任务统计
        taskStats = new JLabel("正在加载...", SwingConstants.CENTER);
        taskStats.setFont(new Font("微软雅黑", Font.PLAIN, 16)); // 从14增大到16

        // 系统状态
        systemStatus = new JLabel("✓ 系统运行正常", SwingConstants.CENTER);
        systemStatus.setFont(new Font("微软雅黑", Font.PLAIN, 16)); // 从14增大到16
        systemStatus.setForeground(new Color(0, 128, 0));

        realtimePanel.add(timeLabel);
        realtimePanel.add(taskStats);
        realtimePanel.add(systemStatus);
    }

    // =============== 新增方法：启动实时更新 ===============
    private void startRealtimeUpdates() {
        // 每秒更新时间
        Timer timeTimer = new Timer(1000, e -> updateTime());
        timeTimer.start();

        // 每30秒更新任务统计和检查任务状态
        Timer statsTimer = new Timer(30000, e -> {
            updateTaskStats();
            checkAndUpdateOverdueTasks();
        });
        statsTimer.start();
        updateTaskStats(); // 立即更新一次
    }

    // =============== 新增方法：更新时间显示 ===============
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeLabel.setText("系统时间: " + sdf.format(new Date()));
    }

    // =============== 新增方法：更新任务统计 ===============
    private void updateTaskStats() {
        SwingUtilities.invokeLater(() -> {
            List<Task> tasks = TaskDAO.getTasksByUser(userId);
            long pending = tasks.stream().filter(t -> "待办".equals(t.getStatus())).count();
            long inProgress = tasks.stream().filter(t -> "进行中".equals(t.getStatus())).count();
            long completed = tasks.stream().filter(t -> "已完成".equals(t.getStatus())).count();

            taskStats.setText(String.format(
                    "任务统计: 总计 %d | 待办 %d | 进行中 %d | 已完成 %d",
                    tasks.size(), pending, inProgress, completed
            ));
        });
    }

    // =============== 新增方法：检查并更新过期任务 ===============
    private void checkAndUpdateOverdueTasks() {
        SwingUtilities.invokeLater(() -> {
            List<Task> tasks = TaskDAO.getTasksByUser(userId);
            LocalDateTime now = LocalDateTime.now();
            boolean updated = false;

            for (Task task : tasks) {
                // 跳过已完成任务
                if ("已完成".equals(task.getStatus())) continue;

                // 如果任务已过期且状态不是已完成
                if (task.getDueDate().isBefore(now)) {
                    task.setStatus("已完成");
                    if (TaskDAO.saveTask(task)) {
                        updated = true;
                    }
                }
            }

            // 如果有更新，刷新表格
            if (updated) {
                refreshTasks();
                JOptionPane.showMessageDialog(this, "检测到过期任务，已自动更新状态为'已完成'");
            }
        });
    }
    // =================================================

    public void refreshTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = TaskDAO.getTasksByUser(userId);

        // 在刷新时也检查一次过期任务
        checkAndUpdateOverdueTasks();

        for (Task task : tasks) {
            // 添加行数据
            tableModel.addRow(new Object[]{
                    task.getTaskId(),
                    task.getTitle(),
                    task.getPriority(),
                    task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    task.getStatus()
            });
        }
        updateTaskStats(); // 刷新任务时同时更新统计
    }

    // =============== 新增自定义比较器：截止时间比较 ===============
    private static class DueDateComparator implements Comparator<String> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        @Override
        public int compare(String dateStr1, String dateStr2) {
            try {
                LocalDateTime date1 = LocalDateTime.parse(dateStr1, formatter);
                LocalDateTime date2 = LocalDateTime.parse(dateStr2, formatter);
                return date1.compareTo(date2);
            } catch (Exception e) {
                return 0; // 解析失败时不做排序
            }
        }
    }

    // =============== 新增自定义比较器：状态比较 ===============
    private static class StatusComparator implements Comparator<String> {
        // 自定义状态顺序
        private final String[] statusOrder = {"待办", "进行中", "已完成"};

        @Override
        public int compare(String status1, String status2) {
            int index1 = getStatusIndex(status1);
            int index2 = getStatusIndex(status2);
            return Integer.compare(index1, index2);
        }

        private int getStatusIndex(String status) {
            for (int i = 0; i < statusOrder.length; i++) {
                if (statusOrder[i].equals(status)) {
                    return i;
                }
            }
            return statusOrder.length; // 未知状态放在最后
        }
    }

    // 添加main方法便于测试
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 模拟用户ID
            MainFrame frame = new MainFrame(1);
            frame.setVisible(true);
        });
    }

    // =============== 新增内部类：统计图表窗口 ===============
    private static class StatisticsFrame extends JFrame {
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
    }
}