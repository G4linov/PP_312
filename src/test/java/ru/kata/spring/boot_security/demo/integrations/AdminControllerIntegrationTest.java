package ru.kata.spring.boot_security.demo.integrations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @Test
    void adminPage_ShouldReturnAdminMenuView() throws Exception {
        // Arrange
        Role userRole = new Role(1L, "ROLE_USER");
        Role adminRole = new Role(2L, "ROLE_ADMIN");

        User user1 = new User();
        user1.setId(1L);
        user1.setRoles(new HashSet<>(Arrays.asList(userRole)));

        User user2 = new User();
        user2.setId(2L);
        user2.setRoles(new HashSet<>(Arrays.asList(adminRole)));

        List<User> users = Arrays.asList(user1, user2);
        List<Role> allRoles = Arrays.asList(userRole, adminRole);

        User currentUser = new User();
        currentUser.setId(3L);
        currentUser.setRoles(new HashSet<>(allRoles)); // У текущего пользователя все роли

        when(userService.getUsers()).thenReturn(users);
        when(roleService.getRoles()).thenReturn(allRoles);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Act & Assert
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminMenu"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("allRoles"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("newUser"));
    }

    @Test
    void updateUser_ShouldRedirectToAdminPage_WhenSuccessful() throws Exception {
        // Arrange
        Role userRole = new Role(1L, "ROLE_USER");
        Role adminRole = new Role(2L, "ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));

        when(userService.getUser(anyLong())).thenReturn(user);
        when(roleService.getRoleById(1L)).thenReturn(userRole);
        when(roleService.getRoleById(2L)).thenReturn(adminRole);

        // Act & Assert
        mockMvc.perform(post("/admin/updateUser")
                        .param("id", "1")
                        .param("username", "updateduser")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("age", "30")
                        .param("email", "updated@example.com")
                        .param("rolesIds", "1", "2")) // Добавляем обе роли
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService).updateUser(any(User.class));
    }

    @Test
    void deleteUser_ShouldRedirectToAdminPage_WhenSuccessful() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>(Arrays.asList(new Role(1L, "ROLE_USER"))));

        when(userService.getUser(anyLong())).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/admin/deleteUser")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService).deleteUser(any(User.class));
    }

    @Test
    void saveUser_ShouldRedirectToAdminPage_WhenNewUserValid() throws Exception {
        // Arrange
        Role userRole = new Role(1L, "ROLE_USER");
        when(roleService.getRoles()).thenReturn(List.of(userRole));

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("new@example.com");
        newUser.setAge(25);
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setRoles(new HashSet<>(Set.of(userRole)));

        // Act & Assert
        mockMvc.perform(post("/admin/saveUser")
                        .flashAttr("user", newUser)
                        .param("rolesIds", "1")) // передаем ID ролей
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService).addUser(any(User.class));
    }


    @Test
    void saveUser_ShouldUpdateUser_WhenUserExists() throws Exception {
        // Arrange
        Role adminRole = new Role(2L, "ROLE_ADMIN");
        when(roleService.getRoles()).thenReturn(List.of(adminRole));

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setPassword("password");
        existingUser.setEmail("existing@example.com");
        existingUser.setAge(30);
        existingUser.setFirstName("Jane");
        existingUser.setLastName("Smith");
        existingUser.setRoles(new HashSet<>(Set.of(adminRole)));

        when(userService.getUser(1L)).thenReturn(existingUser);

        // Act & Assert
        mockMvc.perform(post("/admin/saveUser")
                        .flashAttr("user", existingUser)
                        .param("rolesIds", "2")) // передаем ID ролей
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService).updateUser(any(User.class));
    }
}