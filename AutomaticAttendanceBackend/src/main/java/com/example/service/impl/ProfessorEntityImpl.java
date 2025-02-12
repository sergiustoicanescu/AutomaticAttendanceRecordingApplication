package com.example.service.impl;

import com.example.entity.ProfessorEntity;
import com.example.repository.ProfessorEntityRepository;
import com.example.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorEntityImpl implements ProfessorService {
    @Autowired
    ProfessorEntityRepository professorEntityRepository;
    @Override
    public void save(ProfessorEntity professor) {
        professorEntityRepository.save(professor);
    }

    @Override
    public Optional<ProfessorEntity> findByUserId(Long userId) {
        return professorEntityRepository.findByUserID(userId);
    }

    @Override
    public Optional<ProfessorEntity> findByEmailOptional(String email) {
        return professorEntityRepository.findByEmail(email);
    }

    @Override
    public ProfessorEntity findByEmail(String email) {
        Optional<ProfessorEntity> professorEntityOptional = findByEmailOptional(email);
        if(professorEntityOptional.isPresent()) {
            return professorEntityOptional.get();
        } else {
            throw new RuntimeException("No professor found with this email: " + email);
        }
    }

    @Override
    public List<ProfessorEntity> getAllThatAreNotInList(List<ProfessorEntity> professors) {
        return professorEntityRepository.getAllThatAreNotInCourse(professors);
    }
}
