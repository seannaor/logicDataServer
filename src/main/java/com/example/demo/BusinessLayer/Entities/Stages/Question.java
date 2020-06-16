package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questions")
public class Question {

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

        public void setQuestionIndex(int questionIndex) {
            this.questionIndex = questionIndex;
        }

        public void setStageID(Stage.StageID stageID) {
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

    public Question(String questionJson){
        this.questionJson = questionJson;
        this.questionID = new QuestionID();
    }

    public Question(int qIdx, QuestionnaireStage questionnaireStage, String questionJson) {
        this.questionID = new QuestionID(qIdx, questionnaireStage.getStageID());
        this.questionnaireStage = questionnaireStage;
        this.questionJson = questionJson;
    }

    //Setters
    public void setQuestionnaireStage(QuestionnaireStage questionnaireStage){
        this.questionnaireStage = questionnaireStage;
        if(questionnaireStage.getStageID() != null) {
            setStageId(questionnaireStage.getStageID());
        }
    }

    public void setQuestionID(QuestionID questionID) {
        this.questionID = questionID;
    }

    public void setQuestionJson(String questionJson) {
        this.questionJson = questionJson;
    }

    public Answer answer(Object data) throws ParseException, FormatException {
        JSONObject jQuestion = (JSONObject)  new JSONParser().parse(questionJson);

        switch ((String) jQuestion.get("questionType")){
            case "open":
            case "american":
            case "multiChoice":
                return new Answer(data.toString(),this);
            default:
                throw new FormatException("american, open or multi-choice question");
        }
    }

    // Getters
    public Stage.StageID getStageID(){
        return this.questionnaireStage.getStageID();
    }

    //TODO: maybe change to Map
    public JSONObject getQuestionJson() {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject)parser.parse(questionJson);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new JSONObject();
        }
    }

    public int getIndex() {
        return questionID.questionIndex;
    }

    public QuestionID getQuestionID() {
        return questionID;
    }

    // for ID propose
    public void setStageId(Stage.StageID stageID){
        this.questionID.setStageID(stageID);
    }

    // for ID propose
    public void setQuestionIndex(int i){
        this.questionID.setQuestionIndex(i);
    }
}
