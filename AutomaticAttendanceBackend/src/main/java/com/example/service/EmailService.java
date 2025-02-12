package com.example.service;

import com.example.entity.EmailEntity;
import com.example.entity.UserRole;

import java.util.List;

public interface EmailService {
    void save(EmailEntity email);

    UserRole getUserRoleByEmail(String email);

    List<String> getAllStudentEmails();
}
