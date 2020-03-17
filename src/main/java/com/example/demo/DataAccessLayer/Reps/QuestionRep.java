package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Stages.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRep extends JpaRepository<Question, Question.QuestionID> {
}