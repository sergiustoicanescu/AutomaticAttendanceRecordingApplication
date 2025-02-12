package com.example.repository;

import com.example.entity.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfessorEntityRepository extends JpaRepository<ProfessorEntity, Long> {
    @Query("SELECT p FROM ProfessorEntity p where p.email = :email")
    Optional<ProfessorEntity> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM ProfessorEntity p where p.userEntity.id = :id")
    Optional<ProfessorEntity> findByUserID(@Param("id") Long userId);

    @Query("SELECT p FROM ProfessorEntity p WHERE p not in :professors")
    List<ProfessorEntity> getAllThatAreNotInCourse(@Param("professors") List<ProfessorEntity> professors);
}
