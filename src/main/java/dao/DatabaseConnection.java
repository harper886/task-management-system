package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // 默认配置
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/task_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    // 动态配置参数
    private static String url = DEFAULT_URL;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;
    private static boolean suppressExceptionLogging = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            if (!suppressExceptionLogging) {
                e.printStackTrace();
            }
        }
    }

    // 新增：重置连接参数到默认值
    public static void resetConnectionParameters() {
        url = DEFAULT_URL;
        user = DEFAULT_USER;
        password = DEFAULT_PASSWORD;
    }

    // 新增：动态设置连接参数
    public static void setConnectionParameters(String url, String user, String password) {
        DatabaseConnection.url = url;
        DatabaseConnection.user = user;
        DatabaseConnection.password = password;
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            if (!suppressExceptionLogging) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void setSuppressExceptionLogging(boolean suppress) {
        suppressExceptionLogging = suppress;
    }
}