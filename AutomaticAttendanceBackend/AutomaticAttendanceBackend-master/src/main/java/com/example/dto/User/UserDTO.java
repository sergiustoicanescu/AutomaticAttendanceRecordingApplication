package com.example.dto.User;

import com.example.entity.UserRole;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String photo;

    public UserDTO(Long id, String name, String email, UserRole role, String photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }
}

