package com.example.demo.Entities.GradingTask;

import com.example.demo.Entities.Grader;
import com.example.demo.Entities.Participant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "graders_to_grading_tasks")
public class GraderToGradingTask {
    @Embeddable
    public static class GraderToGradingTaskID implements Serializable {
        private int grading_task_id;
        private int grader_email;
    }

    @EmbeddedId
    private GraderToGradingTaskID graderToGradingTaskID;
    @MapsId("grading_task_id")
    @ManyToOne
    @JoinColumn(name = "grading_task_id")
    private GradingTask gradingTask;
    @MapsId("grader_email")
    @ManyToOne
    @JoinColumn(name = "grader_email")
    private Grader grader;
    private String grader_access_code;
    @ManyToMany
    @JoinTable(
            name = "graders_grading_tasks_to_participants",
            joinColumns = {
                    @JoinColumn(name = "grading_task_id"),
                    @JoinColumn(name = "grader_email")},
            inverseJoinColumns = {@JoinColumn(name = "participant_id")}
    )
    private Set<Participant> participants;
}
