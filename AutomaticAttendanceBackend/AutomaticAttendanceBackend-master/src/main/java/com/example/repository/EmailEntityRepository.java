package com.example.repository;

import com.example.entity.EmailEntity;
import com.example.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailEntityRepository extends JpaRepository<EmailEntity, Long> {
    @Query("SELECT e.role FROM EmailEntity e WHERE e.email = :email")
    UserRole getUserRoleByEmail(@Param("email") String email);
    @Query("SELECT e.email FROM EmailEntity e WHERE e.role='STUDENT'")
    List<String> getAllStudentEmails();
}
