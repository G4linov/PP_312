package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "")
    public String index(Model model) {

        model.addAttribute("allRoles", roleService.getRoles());
        model.addAttribute("users", userService.getUsers());

        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("newUser", new User());

        return "adminMenu";
    }

    @PostMapping(value = "/updateUser")
    @Valid
    public String updateUser(@RequestParam("id") Long id,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("age") int age,
                             @RequestParam("email") String email,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "rolesIds", required = false) String[] rolesIds,
                             Model model) {
        User user = userService.getUser(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        if (password != null) {
            user.setPassword(password);
        }

        if (rolesIds != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleId : rolesIds) {
                Role role = roleService.getRoleById(Long.parseLong(roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin";
    }

    @PostMapping(value = "/deleteUser")
    public String deleteUser(@RequestParam("id") Long id) {
        User user = userService.getUser(id);
        userService.deleteUser(user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/saveUser")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "adminMenu";
        }

        if (user.getId() == null) {
            userService.addUser(user);
        } else {
            userService.updateUser(user);
        }
        return "redirect:/admin";
    }
}
