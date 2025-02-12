package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="User")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Column(name = "Role")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
