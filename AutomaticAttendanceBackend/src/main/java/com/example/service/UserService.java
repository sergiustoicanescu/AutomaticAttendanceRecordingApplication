package com.example.service;

import com.example.entity.UserEntity;

import java.util.Optional;

public interface UserService {

    Optional<UserEntity> findByEmailOptional(String email);

    UserEntity findByEmail(String email);

    void save(UserEntity user);
}
