package com.example.controller;

import com.example.entity.CourseEntity;
import com.example.entity.UserEntity;
import com.example.service.CourseOwnersService;
import com.example.service.CourseService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomSecurityService {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseOwnersService courseOwnersService;

    public boolean canUserAccessByUserID(Long userId, Authentication authentication) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        Optional<UserEntity> user = userService.findByEmailOptional(email);
        return user.filter(userEntity -> userId.equals(userEntity.getId())).isPresent();
    }

    public boolean canUserAccessByCourseCode(String code, Authentication authentication) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        UserEntity user = userService.findByEmail(email);
        CourseEntity courseEntity = courseService.getCourseByCode(code);
        return courseOwnersService.findByCourseAndUser(courseEntity, user).isPresent();
    }

    public boolean canUserAccessByCourseCodeForOwner(String code, Authentication authentication) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        UserEntity user = userService.findByEmail(email);
        return courseService.findByCodeAndUser(code, user).isPresent();
    }

}
