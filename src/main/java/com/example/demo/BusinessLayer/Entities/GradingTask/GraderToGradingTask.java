package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.Participant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "graders_to_grading_tasks")
public class GraderToGradingTask {
    @Embeddable
    public static class GraderToGradingTaskID implements Serializable {
        @Column(name = "grading_task_id")
        private int gradingTaskId;
        @Column(name = "grader_email")
        private String graderEmail;

        public GraderToGradingTaskID() { }

        public GraderToGradingTaskID(int gradingTaskId, String graderEmail) {
            this.gradingTaskId = gradingTaskId;
            this.graderEmail = graderEmail;
        }
    }

    @EmbeddedId
    private GraderToGradingTaskID graderToGradingTaskID;
    @MapsId("gradingTaskId")
    @ManyToOne
    @JoinColumn(name = "grading_task_id")
    private GradingTask gradingTask;
    @MapsId("graderEmail")
    @ManyToOne
    @JoinColumn(name = "grader_email")
    private Grader grader;
    @Column(name = "grader_access_code")
    private String graderAccessCode;
    @ManyToMany
    @JoinTable(
            name = "graders_grading_tasks_to_participants",
            joinColumns = {
                    @JoinColumn(name = "grading_task_id", referencedColumnName = "grading_task_id"),
                    @JoinColumn(name = "grader_email", referencedColumnName = "grader_email")},
            inverseJoinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "participant_id")}
    )
    private List<Participant> participants;

    public GraderToGradingTask() { }

    public GraderToGradingTask(GradingTask gradingTask,Grader grader) {
        this.gradingTask = gradingTask;
        this.grader=grader;
    }

    public GraderToGradingTask(GradingTask gradingTask, Grader grader, String graderAccessCode, List<Participant> participants) {
        this.graderToGradingTaskID = new GraderToGradingTaskID(gradingTask.getGradingTaskId(), grader.getGraderEmail());
        this.gradingTask = gradingTask;
        this.grader = grader;
        this.graderAccessCode = graderAccessCode;
        this.participants = participants;
    }

    public GraderToGradingTaskID getGraderToGradingTaskID() {
        return graderToGradingTaskID;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant p){
        participants.add(p);
    }

    public GradingTask getGradingTask() {
        return gradingTask;
    }

    public Grader getGrader() {
        return grader;
    }

    public String getGraderAccessCode() {
        return graderAccessCode;
    }
}