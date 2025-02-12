package com.example.service;

import com.example.entity.ProfessorEntity;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    void save(ProfessorEntity professor);

    Optional<ProfessorEntity> findByUserId(Long userId);

    Optional<ProfessorEntity> findByEmailOptional(String email);

    ProfessorEntity findByEmail(String email);

    List<ProfessorEntity> getAllThatAreNotInList(List<ProfessorEntity> professors);
}
