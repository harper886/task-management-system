package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(user, "User object should not be null");
        assertEquals(0, user.getUserId(), "Default userId should be 0");
        assertNull(user.getUsername(), "Default username should be null");
        assertNull(user.getPassword(), "Default password should be null");
        assertNull(user.getEmail(), "Default email should be null");
        assertNull(user.getRole(), "Default role should be null");
    }

    @Test
    public void testParameterizedConstructor() {
        User paramUser = new User(1, "testuser", "password123", "test@example.com", "ADMIN");

        assertEquals(1, paramUser.getUserId(), "User ID should match");
        assertEquals("testuser", paramUser.getUsername(), "Username should match");
        assertEquals("password123", paramUser.getPassword(), "Password should match");
        assertEquals("test@example.com", paramUser.getEmail(), "Email should match");
        assertEquals("ADMIN", paramUser.getRole(), "Role should match");
    }

    @Test
    public void testUserIdSetterAndGetter() {
        user.setUserId(100);
        assertEquals(100, user.getUserId(), "User ID getter/setter should work correctly");

        user.setUserId(-5);
        assertEquals(-5, user.getUserId(), "Should handle negative user IDs");
    }

    @Test
    public void testUsernameSetterAndGetter() {
        user.setUsername("johndoe");
        assertEquals("johndoe", user.getUsername(), "Username getter/setter should work correctly");

        user.setUsername("");
        assertEquals("", user.getUsername(), "Should handle empty username");

        user.setUsername(null);
        assertNull(user.getUsername(), "Should handle null username");
    }

    @Test
    public void testPasswordSetterAndGetter() {
        user.setPassword("securePassword123!");
        assertEquals("securePassword123!", user.getPassword(), "Password getter/setter should work correctly");

        user.setPassword("");
        assertEquals("", user.getPassword(), "Should handle empty password");

        user.setPassword(null);
        assertNull(user.getPassword(), "Should handle null password");
    }

    @Test
    public void testEmailSetterAndGetter() {
        user.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", user.getEmail(), "Email getter/setter should work correctly");

        user.setEmail("invalid-email");
        assertEquals("invalid-email", user.getEmail(), "Should handle invalid email formats");

        user.setEmail(null);
        assertNull(user.getEmail(), "Should handle null email");
    }

    @Test
    public void testRoleSetterAndGetter() {
        user.setRole("USER");
        assertEquals("USER", user.getRole(), "Role getter/setter should work correctly");

        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole(), "Should handle different roles");

        user.setRole("");
        assertEquals("", user.getRole(), "Should handle empty role");

        user.setRole(null);
        assertNull(user.getRole(), "Should handle null role");
    }

    @Test
    public void testAllPropertiesTogether() {
        user.setUserId(42);
        user.setUsername("alice");
        user.setPassword("alicePass");
        user.setEmail("alice@wonderland.com");
        user.setRole("GUEST");

        assertEquals(42, user.getUserId(), "User ID should be set");
        assertEquals("alice", user.getUsername(), "Username should be set");
        assertEquals("alicePass", user.getPassword(), "Password should be set");
        assertEquals("alice@wonderland.com", user.getEmail(), "Email should be set");
        assertEquals("GUEST", user.getRole(), "Role should be set");
    }
}