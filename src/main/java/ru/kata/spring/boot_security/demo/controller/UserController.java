package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {

        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", user);

        return "user";
    }

    @GetMapping("/debugRoles")
    @ResponseBody
    public String debugRoles(Authentication authentication) {
        return "User Roles: " + authentication.getAuthorities();
    }
}
