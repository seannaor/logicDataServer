package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Exceptions.ExistException;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "grader_to_participant")
public class GraderToParticipant {
    @Embeddable
    public static class GraderToParticipantID implements Serializable {
        @Column(name = "grading_task_id")
        private int gradingTaskId;
        @Column(name = "grader_email")
        private String graderEmail;
        @Column(name = "expee_participant_id")
        private int expeeParticipantId;

        public GraderToParticipantID() {
        }

        public GraderToParticipantID(int gradingTaskId, String graderEmail, int expeeParticipantId) {
            this.gradingTaskId = gradingTaskId;
            this.graderEmail = graderEmail;
            this.expeeParticipantId = expeeParticipantId;
        }
    }

    @EmbeddedId
    private GraderToParticipantID graderToParticipantID;
    @Column(name = "grading_state")
    private boolean gradingState = false;
    @ManyToOne
    @MapsId("graderToGradingTaskID")
    @JoinColumns({
            @JoinColumn(name = "grading_task_id", referencedColumnName = "grading_task_id"),
            @JoinColumn(name = "grader_email", referencedColumnName = "grader_email")}
    )
    private GraderToGradingTask graderToGradingTask;
    @ManyToOne
    @MapsId("participantId")
    @JoinColumn(name = "expee_participant_id", referencedColumnName = "participant_id")
    private Participant expeeParticipant;
    @OneToOne
    @JoinColumn(name = "grader_participant_id", referencedColumnName = "participant_id")
    private Participant graderParticipant;

    public GraderToParticipant() {
    }

    public GraderToParticipant(GraderToGradingTask graderToGradingTask, Participant expeeParticipant) throws ExistException {
        this.graderToParticipantID = new GraderToParticipantID(graderToGradingTask.getGradingTask().getGradingTaskId(),
                graderToGradingTask.getGrader().getGraderEmail(), expeeParticipant.getParticipantId());
        this.graderToGradingTask = graderToGradingTask;
        this.expeeParticipant = expeeParticipant;
        this.graderParticipant = new Participant(graderToGradingTask.getGradingTask().getGradingExperiment());

        this.graderToGradingTask.addGraderToParticipant(this);

    }

    //setters
    public void setExpeeParticipant(Participant expeeParticipant) {
        this.expeeParticipant = expeeParticipant;
    }

    public void setGraderToGradingTask(GraderToGradingTask graderToGradingTask) {
        this.graderToGradingTask = graderToGradingTask;
    }

    public void setGradingState(boolean gradingState) {
        this.gradingState = gradingState;
    }

    //getters
    public boolean getGradingState() {
        return this.gradingState;
    }

    public GraderToGradingTask getGraderToGradingTask() {
        return graderToGradingTask;
    }

    public Participant getGraderParticipant() {
        return graderParticipant;
    }

    public Participant getExpeeParticipant() {
        return expeeParticipant;
    }

}
