package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "graders")
public class Grader {
    @Id
    @Column(name = "grader_email")
    private String graderEmail;
    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;
    @OneToMany(mappedBy = "grader")
    private Set<GraderToGradingTask> assignedGradingTasks = new HashSet<>();

    public Grader() { }

    public Grader(String graderEmail, Participant participant) {
        this.graderEmail = graderEmail;
        this.participant = participant;
    }

    public String getGraderEmail() {
        return graderEmail;
    }

    public void setGraderEmail(String graderEmail) {
        this.graderEmail = graderEmail;
    }

    public Set<GraderToGradingTask> getAssignedGradingTasks() {
        return assignedGradingTasks;
    }

    public void assignGradingTasks(GraderToGradingTask gradingTask) {
        this.assignedGradingTasks.add(gradingTask);
    }
}
