package com.example.demo.Entities.Stages;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questions")
public class Question {

    @Embeddable
    public static class QuestionID implements Serializable {
        private Stage.StageID stageID;
        int questionIndex;
    }

    @EmbeddedId
    private QuestionID questionID;
    @MapsId("stageID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index"),
            @JoinColumn(name = "experiment_id")
    })
    private QuestionnaireStage questionnaireStage;
}
