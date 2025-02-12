package com.example.dto.User;

import com.example.entity.UserEntity;

public class UserMapper {
    public static UserDTO toDTO(UserEntity user, String photo) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                photo
        );
    }


}
