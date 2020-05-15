package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
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
    @MapsId("participantId")
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;
    @MapsId("questionID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id"),
            @JoinColumn(name = "question_index", referencedColumnName = "question_index")
    })
    private Question question;

    @Column(name = "answer_json", columnDefinition = "json")
    private String answerJson;

    public Answer (){ }

    public Answer (String answerJson, Question question, Participant participant) {
        this.answerID = new AnswerID(participant.getParticipantId(), question.getQuestionID());
        this.answerJson = answerJson;
        this.question = question;
        this.participant = participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
        this.answerID = new AnswerID(participant.getParticipantId(),this.question.getQuestionID());
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Stage.StageID getStageID(){
        return this.question.getStageID();
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getAnswerJson() {
        return answerJson;
    }

    public void setAnswerJson(String answerJson) {
        this.answerJson = answerJson;
    }

}
