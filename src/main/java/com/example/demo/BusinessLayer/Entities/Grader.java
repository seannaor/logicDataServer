package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "graders")
public class Grader {
    @Id
    @Column(name = "grader_email")
    private String graderEmail;
    @OneToMany(mappedBy = "grader")
    private List<GraderToGradingTask> assignedGradingTasks = new ArrayList<>();

    public Grader() { }

    public Grader(String graderEmail) {
        this.graderEmail = graderEmail;
    }

    public String getGraderEmail() {
        return graderEmail;
    }

    public void setGraderEmail(String graderEmail) {
        this.graderEmail = graderEmail;
    }

    public List<GraderToGradingTask> getAssignedGradingTasks() {
        return assignedGradingTasks;
    }

    public void assignGradingTasks(GraderToGradingTask gradingTask) {
        this.assignedGradingTasks.add(gradingTask);
    }

}
