package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Stages.Question;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "answers")
public class Answer {
    @Embeddable
    public static class AnswerID implements Serializable {
        @Column(name = "participant_id")
        private int participantId;
        private Question.QuestionID questionID;

        public AnswerID() {
        }

        public AnswerID(int participantId, Question.QuestionID questionID) {
            this.participantId = participantId;
            this.questionID = questionID;
        }

        public void setQuestionID(Question.QuestionID questionID){
            this.questionID = questionID;
        }

        public void setParticipantId(int participantId){
            this.participantId = participantId;
        }
    }

    @EmbeddedId
    private AnswerID answerID;

    @MapsId("resultID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "participant_id", referencedColumnName = "participant_id"),
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private QuestionnaireResult questionnaireResult;

    @MapsId("questionID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id"),
            @JoinColumn(name = "question_index", referencedColumnName = "question_index")
    })
    private Question question;

    @Column(name = "answer")
    private String answer;

    public Answer (){ }

    public Answer (String answer, Question question, QuestionnaireResult questionnaireResult) {
        this.answerID = new AnswerID(questionnaireResult.getParticipant().getParticipantId(), question.getQuestionID());
        this.answer = answer;
        this.question = question;
        this.questionnaireResult = questionnaireResult;
        this.questionnaireResult.addAns(this);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Stage.StageID getStageID(){
        return this.question.getStageID();
    }

    public QuestionnaireResult getQuestionnaireResult() {
        return questionnaireResult;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
