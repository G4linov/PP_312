package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can't be empty")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+$", message = "Name can't contain numbers or special characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password can't be empty")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$", message = "First name must contain only letters")
    @Column(name = "first_name")
    private String firstName;

    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$", message = "Last name must contain only letters")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @Min(value = 6, message = "Age must be at least 7")
    @Max(value = 100, message = "Age must be less than 100")
    @Column(nullable = false)
    private int age;

    @Column( name = "user_info")
    private String userInfo;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}