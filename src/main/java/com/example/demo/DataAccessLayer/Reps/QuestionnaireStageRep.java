package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireStageRep extends JpaRepository<QuestionnaireStage, Stage.StageID> {
}