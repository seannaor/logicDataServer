package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.Participant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "graders_to_grading_tasks")
public class GraderToGradingTask {

    @Embeddable
    public static class GraderToGradingTaskID implements Serializable {
        private int gradingTaskId;
        private int graderEmail;
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
                    @JoinColumn(name = "grading_task_id"),
                    @JoinColumn(name = "grader_email")},
            inverseJoinColumns = {@JoinColumn(name = "participant_id")}
    )
    private Set<Participant> participants = new HashSet<>();

    public GraderToGradingTask(GradingTask gradingTask) {
        this.gradingTask = gradingTask;
    }

    public GraderToGradingTask(GradingTask gradingTask,Grader grader) {
        this.gradingTask = gradingTask;
        this.grader=grader;
    }

    public String getGraderAccessCode() {
        return graderAccessCode;
    }

    public Grader getGrader() {
        return grader;
    }

    public GradingTask getGradingTask() {
        return gradingTask;
    }

    public void addParticipant(Participant p){
        participants.add(p);
    }
}