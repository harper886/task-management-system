package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

        // 新增：回收站按钮
        JButton trashButton = new JButton("回收站");

        // 新增排序按钮
        JButton sortByDueDateButton = new JButton("按截止时间排序");
        JButton sortByStatusButton = new JButton("按状态排序");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(trashButton); // 添加回收站按钮
        buttonPanel.add(sortByDueDateButton);
        buttonPanel.add(sortByStatusButton);

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

        // 创建排序器并设置
        sorter = new TableRowSorter<>(tableModel);
        tasksTable.setRowSorter(sorter);

        // 设置截止时间列的排序器
        sorter.setComparator(3, new DueDateComparator());

        // 设置状态列的排序器
        sorter.setComparator(4, new StatusComparator());

        JScrollPane scrollPane = new JScrollPane(tasksTable);

        // =============== 新增实时面板 ===============
        createRealtimePanel();

        // 创建北部容器，包含按钮面板和实时面板
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(buttonPanel, BorderLayout.NORTH);
        northContainer.add(realtimePanel, BorderLayout.CENTER);

        panel.add(northContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        refreshTasks();

        // 启动实时更新
        startRealtimeUpdates();

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
    }

    // =============== 新增方法：创建实时面板 ===============
    private void createRealtimePanel() {
        realtimePanel = new JPanel(new GridLayout(1, 3, 10, 0));
        realtimePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 10, 0),
                BorderFactory.createTitledBorder("实时信息")
        ));
        realtimePanel.setBackground(new Color(240, 245, 255));  // 设置浅蓝色背景

        // 系统时间显示
        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        updateTime(); // 初始更新时间

        // 任务统计
        taskStats = new JLabel("正在加载...", SwingConstants.CENTER);
        taskStats.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        // 系统状态
        systemStatus = new JLabel("✓ 系统运行正常", SwingConstants.CENTER);
        systemStatus.setFont(new Font("微软雅黑", Font.PLAIN, 13));
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
}