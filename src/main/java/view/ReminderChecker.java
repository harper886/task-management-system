package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderChecker {
    private Timer timer;
    private int userId;
    private JFrame parentFrame;

    public ReminderChecker(int userId, JFrame parentFrame) {
        this.userId = userId;
        this.parentFrame = parentFrame;
        startReminderCheck();
    }

    private void startReminderCheck() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkUpcomingTasks();
            }
        }, 0, 5 * 60 * 1000); // 每5分钟检查一次
    }

    private void checkUpcomingTasks() {
        List<Task> tasks = TaskDAO.getTasksByUser(userId);
        LocalDateTime now = LocalDateTime.now();

        for (Task task : tasks) {
            if (task.getStatus().equals("已完成")) continue;

            Duration duration = Duration.between(now, task.getDueDate());
            long hours = duration.toHours();

            if (hours > 0 && hours <= 24) {
                showReminder(task, hours);
            }
        }
    }

    private void showReminder(Task task, long hoursLeft) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parentFrame,
                    "任务即将到期: " + task.getTitle() + "\n" +
                            "截止时间: " + task.getDueDate() + "\n" +
                            "剩余时间: " + hoursLeft + "小时",
                    "任务提醒",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}