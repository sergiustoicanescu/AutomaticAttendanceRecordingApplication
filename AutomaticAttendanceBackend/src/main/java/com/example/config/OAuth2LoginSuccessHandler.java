package com.example.config;

import com.example.entity.ProfessorEntity;
import com.example.entity.StudentEntity;
import com.example.entity.UserEntity;
import com.example.entity.UserRole;
import com.example.service.EmailService;
import com.example.service.ProfessorService;
import com.example.service.StudentService;
import com.example.service.UserService;
import com.example.services.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    private final StudentService studentService;

    private final ProfessorService professorService;

    private final EmailService emailService;

    private final CookieService cookieService = new CookieService();

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${domain.url}")
    private String domain;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        userService.findByEmailOptional(email).ifPresentOrElse(user -> {
            DefaultOAuth2User newUser = new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(user.getRole().name())),
                    attributes, "sub");
            Authentication securityAuth = new OAuth2AuthenticationToken(
                    newUser,
                    List.of(new SimpleGrantedAuthority(user.getRole().name())),
                    oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
            SecurityContextHolder.getContext().setAuthentication(securityAuth);
        }, () -> {
            UserRole userRole = emailService.getUserRoleByEmail(email);
            if (userRole == null) {
                request.getSession().invalidate();
                throw new AuthenticationServiceException("Email not found in authorized list");
            }

            UserEntity userEntity = new UserEntity();
            userEntity.setRole(userRole);
            userEntity.setEmail(email);
            userEntity.setName(name);
            userService.save(userEntity);

            String firstName = (String) attributes.get("given_name");
            String lastName = (String) attributes.get("family_name");

            if(userRole.equals(UserRole.STUDENT)) {
                Optional<StudentEntity> existingStudent = studentService.findByEmailOptional(email);

                StudentEntity studentEntity;
                if (existingStudent.isPresent()) {
                    studentEntity = existingStudent.get();
                    studentEntity.setFirstName(firstName);
                    studentEntity.setLastName(lastName);
                    studentEntity.setUserEntity(userEntity);
                } else {
                    studentEntity = new StudentEntity();
                    studentEntity.setFirstName(firstName);
                    studentEntity.setLastName(lastName);
                    studentEntity.setEmail(email);
                    studentEntity.setUserEntity(userEntity);
                }
                studentService.save(studentEntity);
            }
            else {
                ProfessorEntity professorEntity = new ProfessorEntity();
                professorEntity.setFirstName(firstName);
                professorEntity.setLastName(lastName);
                professorEntity.setEmail(email);
                professorEntity.setUserEntity(userEntity);
                professorService.save(professorEntity);
            }

            DefaultOAuth2User newUser = new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(userEntity.getRole().name())),
                    attributes, "sub");
            Authentication securityAuth = new OAuth2AuthenticationToken(
                    newUser,
                    List.of(new SimpleGrantedAuthority(userEntity.getRole().name())),
                    oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
            SecurityContextHolder.getContext().setAuthentication(securityAuth);
        });


        String preLoginPath = cookieService.findCookieValue("preLoginPath", request).orElse(frontendUrl);
        if(!preLoginPath.contains(frontendUrl)){
            preLoginPath = frontendUrl;
        }
        cookieService.clearCookie("preLoginPath", response, domain);

        getRedirectStrategy().sendRedirect(request, response, preLoginPath);
    }
}
