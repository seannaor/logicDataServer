package com.example.demo.BusinessLayer.Entities.Stages;

import net.minidev.json.JSONObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questions")
public class Question {

    public QuestionID getQuestionID() {
        return questionID;
    }

    public void setQuestionID(QuestionID questionID) {
        this.questionID = questionID;
    }

    public String getQuestionJson() {
        return questionJson;
    }

    public void setQuestionJson(String questionJson) {
        this.questionJson = questionJson;
    }

    @Embeddable
    public static class QuestionID implements Serializable {
        @Column(name = "question_index")
        int questionIndex;
        private Stage.StageID stageID;

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
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private QuestionnaireStage questionnaireStage;

    @Column(name = "question_json", columnDefinition = "json")
    private String questionJson;

    public Question() {
    }

    public Question(int qIdx, QuestionnaireStage questionnaireStage, String questionJson) {
        this.questionID = new QuestionID(qIdx, questionnaireStage.getStageID());
        this.questionnaireStage = questionnaireStage;
        this.questionJson = questionJson;

//        answer = new ArrayList<>();
    }
}
