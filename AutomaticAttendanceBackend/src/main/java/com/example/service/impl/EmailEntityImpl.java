package com.example.service.impl;

import com.example.entity.EmailEntity;
import com.example.entity.UserRole;
import com.example.repository.EmailEntityRepository;
import com.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailEntityImpl implements EmailService {

    @Autowired
    EmailEntityRepository emailEntityRepository;

    @Override
    public void save(EmailEntity email) {
        emailEntityRepository.save(email);
    }

    @Override
    public UserRole getUserRoleByEmail(String email) {
        return emailEntityRepository.getUserRoleByEmail(email);
    }

    @Override
    public List<String> getAllStudentEmails() {
        return emailEntityRepository.getAllStudentEmails();
    }
}
