package com.example.service.impl;

import com.example.entity.UserEntity;
import com.example.repository.UserEntityRepository;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Override
    public Optional<UserEntity> findByEmailOptional(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        Optional<UserEntity> userEntityOptional = findByEmailOptional(email);
        if(userEntityOptional.isPresent()) {
            return userEntityOptional.get();
        } else {
            throw new RuntimeException("No user found with this email: " + email);
        }
    }

    @Override
    public void save(UserEntity user) {
        userEntityRepository.save(user);
    }
}
