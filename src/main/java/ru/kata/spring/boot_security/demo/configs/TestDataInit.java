package ru.kata.spring.boot_security.demo.configs;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Component
public class TestDataInit {
    private final UserService userService;

    public TestDataInit(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        if (userService.getUsers().isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setEmail("admin@email.com");
            admin.setAge(22);
            admin.setRoles(Collections.singleton(new Role(1L, "ROLE_ADMIN")));

            userService.addUser(admin);

            System.out.println("Админ создан: login=admin, password=admin");
        }
    }
}