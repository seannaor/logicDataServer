package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Experiment;

import javax.persistence.*;

@Entity
@Table(name = "grading_tasks")
public class GradingTask {
    @Id
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
}
