package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
public class UserService  {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        this.userRepository.save(user);
    }

    public void updateUser(User user) {
        this.addUser(user);
    }

    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }

    public User getUser(int id) {
        return this.userRepository.findUserById(id);
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
}