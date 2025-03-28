package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Set<Role> getRolesByIds(Set<Long> rolesIds) {
        return new HashSet<Role>(roleRepository.findAllById(rolesIds));
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
}
