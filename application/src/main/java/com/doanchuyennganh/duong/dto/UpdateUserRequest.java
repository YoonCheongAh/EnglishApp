package com.doanchuyennganh.duong.dto;

import com.doanchuyennganh.duong.model.Role;

public class UpdateUserRequest {
    private String fullname;
    private String password;
    private Role role;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
}
