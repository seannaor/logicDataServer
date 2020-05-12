package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Exceptions.ExistException;

import javax.persistence.*;
import javax.servlet.http.Part;
import java.io.Serializable;

@Entity
@Table(name = "graders_grading_tasks_to_participants")
public class GradersGTToParticipants {
    @Embeddable
    public static class GradersGTToParticipantsID implements Serializable {
        @Column(name = "grading_task_id")
        private int gradingTaskId;
        @Column(name = "grader_email")
        private String graderEmail;
        @Column(name = "participant_id")
        private int participantId;

        public GradersGTToParticipantsID() { }

        public GradersGTToParticipantsID(int gradingTaskId, String graderEmail, int participantId) {
            this.gradingTaskId = gradingTaskId;
            this.graderEmail = graderEmail;
            this.participantId = participantId;
        }
    }
    @EmbeddedId
    private GradersGTToParticipantsID gradersGTToParticipantsID;
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
    @JoinColumn(name = "participant_id", referencedColumnName = "participant_id")
    private Participant participant;

    //TODO: I think i need to add Participant for the grader side

    public GradersGTToParticipants() { }
    public GradersGTToParticipants(GraderToGradingTask graderToGradingTask, Participant participant) {
        this.gradersGTToParticipantsID = new GradersGTToParticipantsID(graderToGradingTask.getGradingTask().getGradingTaskId(), graderToGradingTask.getGrader().getGraderEmail(), participant.getParticipantId());
        this.graderToGradingTask = graderToGradingTask;
        this.participant = participant;
        try {
            this.graderToGradingTask.addGradersGTToParticipants(this);
        } catch (ExistException e) {
            e.printStackTrace();
        }
        this.participant.addGradersGTToParticipants(this);

    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }


    public GraderToGradingTask getGraderToGradingTask() {
        return graderToGradingTask;
    }

    public void setGraderToGradingTask(GraderToGradingTask graderToGradingTask) {
        this.graderToGradingTask = graderToGradingTask;
    }

    public boolean isGradingState() {
        return gradingState;
    }

    public void setGradingState(boolean gradingState) {
        this.gradingState = gradingState;
    }
    public boolean getGradingState() {
        return this.gradingState;
    }

}
