package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Question;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "answers")
public class Answer {

    @Embeddable
    public static class AnswerID implements Serializable {
        private int participantId;
        private Question.QuestionID questionID;
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
            @JoinColumn(name = "stage_index"),
            @JoinColumn(name = "experiment_id"),
            @JoinColumn(name = "question_index")
    })
    private Question question;
}
