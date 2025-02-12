package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="Student")
@Data
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_data_id")
    private UserEntity userEntity;

    private String firstName;

    private String lastName;

    private String email;

}
