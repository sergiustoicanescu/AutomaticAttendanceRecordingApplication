package com.example.dto.Professor;

import com.example.entity.ProfessorEntity;

import java.util.ArrayList;
import java.util.List;

public class ProfessorMapper {

    public static ProfessorDTO toDTO(ProfessorEntity professor) {
        return new ProfessorDTO(
                professor.getFirstName(),
                professor.getLastName(),
                professor.getEmail()
        );
    }

    public static List<ProfessorDTO> toDTO(List<ProfessorEntity> professors) {
        List<ProfessorDTO> professorDTOs = new ArrayList<>();
        for(ProfessorEntity professor : professors) {
            ProfessorDTO professorDTO = toDTO(professor);
            professorDTOs.add(professorDTO);
        }
        return professorDTOs;
    }

}
