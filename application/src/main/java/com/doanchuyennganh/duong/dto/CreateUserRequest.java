package com.doanchuyennganh.duong.dto;

import com.doanchuyennganh.duong.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String fullname;
    private String email;
    private String username;
    private String password;
    private Role role; // USER hoặc ADMIN

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
