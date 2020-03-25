package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "grading_tasks")
public class GradingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grading_task_id")
    private int gradingTaskId;
    @OneToOne
    @JoinColumn(name = "base_experiment", referencedColumnName = "experiment_id")
    private Experiment baseExperiment;
    @OneToOne
    @JoinColumn(name = "general_experiment", referencedColumnName = "experiment_id")
    private Experiment generalExperiment;
    @ManyToOne
    @JoinColumn(name = "grading_experiment", referencedColumnName = "experiment_id")
    private Experiment gradingExperiment;
    @OneToMany(mappedBy = "gradingTask")
    private Set<GraderToGradingTask> assignedGradingTasks = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "stages_of_grading_task",
            joinColumns = {
                    @JoinColumn(name = "grading_task_id", referencedColumnName = "grading_task_id"),
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
                    @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")}
    )
    private Set<Stage> stages;

    public GradingTask() { }

    public GradingTask(Experiment baseExperiment, Experiment generalExperiment, Experiment gradingExperiment, Set<Stage> stages) {
        this.baseExperiment = baseExperiment;
        this.generalExperiment = generalExperiment;
        this.gradingExperiment = gradingExperiment;
        this.stages = stages;
    }

    public int getGradingTaskId() {
        return gradingTaskId;
    }

    public Set<Stage> getStages() {
        return stages;
    }

    public void setStages(Set<Stage> stages) {
        this.stages = stages;
    }

    public Set<GraderToGradingTask> getAssignedGradingTasks() {
        return assignedGradingTasks;
    }

    public void addAssignedGradingTasks(GraderToGradingTask assignedGradingTask) {
        this.assignedGradingTasks.add(assignedGradingTask);
    }
}
