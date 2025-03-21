package ru.kata.spring.boot_security.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserService userService) {
		return args -> {
			if (userService.getUsers().isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword("admin");
				admin.setFirstName("admin");
				admin.setLastName("admin");
				admin.setEmail("admin@email.com");
				admin.setAge(22);

				userService.addUser(admin);

				System.out.println("Админ создан: login=admin, password=admin");
			}
		};
	}

}
