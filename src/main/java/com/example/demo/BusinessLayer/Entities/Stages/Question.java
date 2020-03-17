package com.example.demo.BusinessLayer.Entities.Stages;

import net.minidev.json.JSONObject;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questions")
public class Question {

    @Embeddable
    public static class QuestionID implements Serializable {
        private Stage.StageID stageID;
        int questionIndex;

        public QuestionID() {
        }

        public QuestionID(int questionIndex, Stage.StageID stageID) {
            this.questionIndex = questionIndex;
            this.stageID = stageID;
        }
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

    public Question() {
    }

    public Question(int qIdx, QuestionnaireStage questionnaireStage, JSONObject jQuestion) {
        this.questionID = new QuestionID(qIdx, questionnaireStage.getStageID());
        this.questionnaireStage = questionnaireStage;

//        this.question_json = question_json.toJSONString();
//        answer = new ArrayList<>();
    }
}
