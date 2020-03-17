package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRep extends JpaRepository<Answer, Answer.AnswerID> {
}

