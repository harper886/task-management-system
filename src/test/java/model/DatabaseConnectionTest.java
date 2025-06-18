package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static dao.DatabaseConnection.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)  // 激活Mockito注解支持[8](@ref)
public class DatabaseConnectionTest {

    @BeforeEach
    @AfterEach
    void resetState() {
        resetConnectionParameters();  // 隔离测试状态[2,4](@ref)
        setSuppressExceptionLogging(false);
    }

    // 核心测试：默认参数连接成功
    @Test
    void getConnection_successWithDefaultParams() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
            // 模拟成功连接
            Connection mockConn = Mockito.mock(Connection.class);
            driverManagerMock.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConn);  // 完整定义Stubbing行为[7](@ref)

            Connection result = getConnection();
            assertNotNull(result);
        }
    }

    // 核心测试：异常抑制功能

    // 边界测试：参数重置功能
    @Test
    void resetConnectionParameters_restoresDefaults() {
        // 修改参数
        setConnectionParameters("jdbc:invalid", "wrong", "creds");

        // 重置验证
        resetConnectionParameters();
        assertEquals(DatabaseConnection.DEFAULT_URL, url);
        assertEquals(DatabaseConnection.DEFAULT_USER, user);
        assertEquals(DatabaseConnection.DEFAULT_PASSWORD, password);
    }
}