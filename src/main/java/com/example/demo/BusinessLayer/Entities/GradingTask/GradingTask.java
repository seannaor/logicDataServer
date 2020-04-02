package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private List<GraderToGradingTask> assignedGradingTasks = new ArrayList<>();
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
    private List<Stage> stages;

    public GradingTask() { }

    public GradingTask(Experiment baseExperiment, Experiment generalExperiment, Experiment gradingExperiment) {
        this.baseExperiment = baseExperiment;
        this.generalExperiment = generalExperiment;
        this.gradingExperiment = gradingExperiment;
    }

    public GradingTask(Experiment baseExperiment, Experiment generalExperiment, Experiment gradingExperiment, List<Stage> stages) {
        this.baseExperiment = baseExperiment;
        this.generalExperiment = generalExperiment;
        this.gradingExperiment = gradingExperiment;
        this.stages = stages;
    }

    public int getGradingTaskId() {
        return gradingTaskId;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<GraderToGradingTask> getAssignedGradingTasks() {
        return assignedGradingTasks;
    }

    public void addAssignedGradingTasks(GraderToGradingTask assignedGradingTask) {
        this.assignedGradingTasks.add(assignedGradingTask);
    }

    public Experiment getBaseExperiment() {
        return baseExperiment;
    }

    public Experiment getGeneralExperiment() {
        return generalExperiment;
    }

    public Experiment getGradingExperiment() {
        return gradingExperiment;
    }
}
