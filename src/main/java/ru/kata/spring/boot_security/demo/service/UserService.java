package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
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

    public User getUser(Long id) {
        return this.userRepository.findUserById(id);
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
}