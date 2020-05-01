package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Exceptions.ExistException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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

        public int getGradingTaskId() {
            return gradingTaskId;
        }

        public String getGraderEmail() {
            return graderEmail;
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
    @OneToMany(mappedBy = "graderToGradingTask")
    private List<GradersGTToParticipants> gradersGTToParticipants = new ArrayList<>();

    public GraderToGradingTask() { }

    public GraderToGradingTask(GradingTask gradingTask,Grader grader) {
        this.gradingTask = gradingTask;
        this.grader=grader;
    }

    public GraderToGradingTask(GradingTask gradingTask, Grader grader, String graderAccessCode) {
        this.graderToGradingTaskID = new GraderToGradingTaskID(gradingTask.getGradingTaskId(), grader.getGraderEmail());
        this.gradingTask = gradingTask;
        this.grader = grader;
        this.graderAccessCode = graderAccessCode;
        this.gradingTask.addAssignedGradingTasks(this);
        this.grader.assignGradingTasks(this);
    }

    public GraderToGradingTaskID getGraderToGradingTaskID() {
        return graderToGradingTaskID;
    }

    public List<GradersGTToParticipants> getGradersGTToParticipants() {
        return this.gradersGTToParticipants;
    }

    public void setGradersGTToParticipants(List<GradersGTToParticipants> gradersGTToParticipants) {
        this.gradersGTToParticipants = gradersGTToParticipants;
    }

    public void addGradersGTToParticipants(GradersGTToParticipants g) throws ExistException {
        if(this.gradersGTToParticipants.contains(g)) throw new ExistException("user with id "+g.getParticipant().getParticipantId(),this.grader.getGraderEmail()+" participants");
        this.gradersGTToParticipants.add(g);
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