package ru.kata.spring.boot_security.demo.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAdminPage() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminMenu"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateValidUser() throws Exception {
        // Убедимся, что роли существуют в базе
        Role userRole = new Role(1L, "ROLE_USER");

        mockMvc.perform(post("/admin/saveUser")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@example.com")
                        .param("age", "30")
                        .param("roles", "1")) // Используем ID роли вместо названия
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        User savedUser = userService.getUserByUsername("newuser");
        assertNotNull(savedUser);
        assertEquals("john@example.com", savedUser.getEmail());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateUserWithValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/saveUser")
                        .param("username", "")  // invalid username
                        .param("password", "short")
                        .param("email", "invalid-email")
                        .param("age", "5")      // below min age
                        .param("roles", "ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("user"))
                .andExpect(model().attributeExists("allRoles"))
                .andExpect(view().name("adminUserInfo"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateUser() throws Exception {
        // Предварительно создаем тестовую роль
        Role adminRole = new Role(1L, "ROLE_ADMIN");

        // Получаем существующего пользователя
        User existingUser = userService.getUserByUsername("admin");
        Long userId = existingUser.getId();

        mockMvc.perform(post("/admin/saveUser")
                        .param("id", userId.toString())
                        .param("username", "adminupdated") // Убираем дефис
                        .param("password", "newpassword")
                        .param("firstName", "Admin Updated")
                        .param("lastName", "Adminov")
                        .param("email", "admin.updated@example.com")
                        .param("age", "35")
                        .param("roles", "1") // Используем ID роли вместо названия
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        User updatedUser = userService.getUser(userId);
        assertEquals("Admin Updated", updatedUser.getFirstName());
        assertEquals("adminupdated", updatedUser.getUsername()); // Проверяем новое имя
        assertTrue(updatedUser.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        User userToDelete = userService.getUserByUsername("admin");
        assertNotNull(userToDelete);

        mockMvc.perform(get("/admin/deleteUser")
                        .param("id", userToDelete.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        assertNull(userService.getUser(userToDelete.getId()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testAdminPageAccessDeniedForUserRole() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAdminPageUnauthenticated() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}