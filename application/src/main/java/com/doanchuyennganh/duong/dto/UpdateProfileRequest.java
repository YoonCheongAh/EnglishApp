package com.doanchuyennganh.duong.dto;

public class UpdateProfileRequest {
    private String fullname;
    private String email;
    private String avatarUrl;
    private String password;

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
