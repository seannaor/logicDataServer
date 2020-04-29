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

    @Lob
    @Column(name = "textual_answer")
    private String textualAnswer;

    @Column(name = "numeral_answer")
    private Integer numeralAnswer;

    public Answer (){ }

    public Answer (String textualAnswer, Question question, Participant participant) {
        this.answerID = new AnswerID(participant.getParticipantId(), question.getQuestionID());
        this.textualAnswer = textualAnswer;
        this.numeralAnswer = null;
        this.question = question;
        this.participant = participant;
    }

    public Answer (Integer numeralAnswer, Question question, Participant participant) {
        this.answerID = new AnswerID(participant.getParticipantId(), question.getQuestionID());
        this.textualAnswer = null;
        this.numeralAnswer = numeralAnswer;
        this.question = question;
        this.participant = participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setTextualAnswer(String textualAnswer) {
        this.textualAnswer = textualAnswer;
    }

    public void setNumeralAnswer(Integer numeralAnswer) {
        this.numeralAnswer = numeralAnswer;
    }

    public Stage.StageID getStageID(){
        return this.question.getStageID();
    }
}
