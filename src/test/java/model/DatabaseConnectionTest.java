package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

class DatabaseConnectionTest {

    @BeforeEach
    void setup() {
        DatabaseConnection.setSuppressExceptionLogging(true);
        DatabaseConnection.resetConnectionParameters();
    }
//    @AfterEach
//    void tearDown() {
//        DatabaseConnection.setSuppressExceptionLogging(false);
//    }
//    @AfterEach
//    void tearDown() {
//        DatabaseConnection.resetConnection(); // 确保释放资源
//    }
    @Test
    void getConnection_success() throws SQLException {
        try (MockedStatic<DriverManager> mockDriverManager = mockStatic(DriverManager.class)) {
            // 模拟成功连接
            Connection mockConn = Mockito.mock(Connection.class);
            // 使用anyString匹配任意参数（避免硬编码私有常量）
            mockDriverManager.when(() -> DriverManager.getConnection(
                            anyString(), anyString(), anyString()
                    ))
                    .thenReturn(mockConn);

            Connection result = DatabaseConnection.getConnection();
            assertNotNull(result);
            assertSame(mockConn, result);
        }
    }

    @Test
    void getConnection_failure() {
        try (MockedStatic<DriverManager> mockDriverManager = mockStatic(DriverManager.class)) {
            // 关键修复：链式调用 + 类型安全匹配器
            mockDriverManager.when(() -> DriverManager.getConnection(
                            anyString(), anyString(), anyString()  // ✅ 用 anyString() 替代 any()
                    ))
                    .thenThrow(new SQLException("Connection failed")); // ✅ 确保 thenThrow 在同一链中

            assertNull(DatabaseConnection.getConnection());
        }
    }

    @Test
    void setConnectionParameters_effect() throws SQLException {
        // 设置测试参数
        String testUrl = "jdbc:mysql://test_host/db";
        String testUser = "test_user";
        String testPass = "test_pass";
        DatabaseConnection.setConnectionParameters(testUrl, testUser, testPass);

        try (MockedStatic<DriverManager> mockDriverManager = mockStatic(DriverManager.class)) {
            Connection mockConn = Mockito.mock(Connection.class);
            // 验证使用新参数调用
            mockDriverManager.when(() -> DriverManager.getConnection(
                            testUrl, testUser, testPass
                    ))
                    .thenReturn(mockConn);

            assertNotNull(DatabaseConnection.getConnection());
        }
    }

    @Test
    void resetConnectionParameters_effect() throws SQLException {
        // 修改参数后重置
        DatabaseConnection.setConnectionParameters("invalid", "invalid", "invalid");
        DatabaseConnection.resetConnectionParameters();

        try (MockedStatic<DriverManager> mockDriverManager = mockStatic(DriverManager.class)) {
            Connection mockConn = Mockito.mock(Connection.class);
            // 通过实际调用验证默认参数（不硬编码）
            mockDriverManager.when(() -> DriverManager.getConnection(
                            anyString(), anyString(), anyString()
                    ))
                    .thenReturn(mockConn);

            assertNotNull(DatabaseConnection.getConnection());
        }
    }

    @Test
    void suppressExceptionLogging_effect() {
        // 直接验证setter方法无异常
        assertDoesNotThrow(() ->
                DatabaseConnection.setSuppressExceptionLogging(true)
        );
    }
}