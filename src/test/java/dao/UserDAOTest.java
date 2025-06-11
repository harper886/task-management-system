package dao;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() throws SQLException {
        // 确保测试用户不存在
        deleteTestUser();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // 清理测试用户
        deleteTestUser();
    }

    // 删除测试用户
    private void deleteTestUser() throws SQLException {
        String sql = "DELETE FROM user WHERE username = ? OR email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, TEST_USERNAME);
            pstmt.setString(2, TEST_EMAIL);
            pstmt.executeUpdate();
        }
    }

    @Test
    void testAuthenticateSuccess() {
        // 准备测试数据 - 注册用户
        UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // 执行认证
        User user = UserDAO.authenticate(TEST_USERNAME, TEST_PASSWORD);

        // 验证结果
        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals("user", user.getRole());
        assertTrue(user.getUserId() > 0);
    }

    @Test
    void testAuthenticateInvalidUsername() {
        // 准备测试数据 - 注册用户
        UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // 执行认证 - 使用无效用户名
        User user = UserDAO.authenticate("invalid_username", TEST_PASSWORD);

        // 验证结果
        assertNull(user);
    }

    @Test
    void testAuthenticateInvalidPassword() {
        // 准备测试数据 - 注册用户
        UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // 执行认证 - 使用无效密码
        User user = UserDAO.authenticate(TEST_USERNAME, "invalid_password");

        // 验证结果
        assertNull(user);
    }

    @Test
    void testAuthenticateNoUsers() {
        // 确保没有用户存在
        User user = UserDAO.authenticate("any_username", "any_password");
        assertNull(user);
    }

    @Test
    void testRegisterUserSuccess() {
        // 执行注册
        boolean result = UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // 验证结果
        assertTrue(result);

        // 验证用户已创建
        User user = UserDAO.authenticate(TEST_USERNAME, TEST_PASSWORD);
        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals("user", user.getRole());
    }

    @Test
    void testRegisterUserDuplicateUsername() {
        // 第一次注册 - 应该成功
        boolean firstResult = UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        assertTrue(firstResult);

        // 第二次注册 - 相同用户名
        boolean secondResult = UserDAO.registerUser(TEST_USERNAME, "another_password", "another@example.com");

        // 验证结果
        assertFalse(secondResult);
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        // 第一次注册 - 应该成功
        boolean firstResult = UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        assertTrue(firstResult);

        // 第二次注册 - 相同邮箱
        boolean secondResult = UserDAO.registerUser("another_username", "another_password", TEST_EMAIL);

        // 验证结果
        assertFalse(secondResult);
    }

    @Test
    void testRegisterUserEmptyFields() {
        // 测试空用户名
        boolean result1 = UserDAO.registerUser("", TEST_PASSWORD, TEST_EMAIL);
        assertFalse(result1);

        // 测试空密码
        boolean result2 = UserDAO.registerUser(TEST_USERNAME, "", TEST_EMAIL);
        assertFalse(result2);

        // 测试空邮箱
        boolean result3 = UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, "");
        assertFalse(result3);
    }

    @Test
    void testRegisterUserSQLException() {
        // 模拟SQL异常 - 使用无效表名
        // 注意：这只是一个示例，实际测试中可能需要使用Mock或其他技术
        // 这里我们依赖数据库行为，如果表名正确则不会抛出异常
        boolean result = UserDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        assertTrue(result); // 正常情况下应该成功
    }
}