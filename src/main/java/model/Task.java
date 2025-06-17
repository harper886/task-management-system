package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int taskId;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime dueDate;
    private String status;
    private int userId;
    private boolean deleted;
    private LocalDateTime deleteTime; // 新增：删除时间
    private LocalDateTime createTime; // 新增：创建时间

    // 默认构造器
    public Task() {
        this.createTime = LocalDateTime.now(); // 设置默认创建时间为当前时间
    }

    // 全字段构造器
    public Task(int taskId, String title, String description, String priority,
                LocalDateTime dueDate, String status, int userId,
                boolean deleted, LocalDateTime deleteTime, LocalDateTime createTime) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.userId = userId;
        this.deleted = deleted;
        this.deleteTime = deleteTime;
        this.createTime = createTime;
    }

    // 简化的全字段构造器（兼容旧代码）
    public Task(int taskId, String title, String description, String priority,
                LocalDateTime dueDate, String status, int userId) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.userId = userId;
        this.createTime = LocalDateTime.now(); // 设置默认创建时间为当前时间
    }

    // Getter 和 Setter 方法
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // 添加toString方法，便于调试和日志输出
    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                ", userId=" + userId +
                ", deleted=" + deleted +
                ", deleteTime=" + deleteTime +
                ", createTime=" + createTime +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId; // 仅根据任务ID判断相等性
    }
    @Override
    public int hashCode() {
        return Objects.hash(taskId); // 仅基于任务ID生成哈希码
    }
}