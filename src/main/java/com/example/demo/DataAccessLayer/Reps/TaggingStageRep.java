package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaggingStageRep extends JpaRepository<TaggingStage, Stage.StageID> {
}
