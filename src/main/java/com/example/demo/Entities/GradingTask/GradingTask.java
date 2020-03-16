package com.example.demo.Entities.GradingTask;

import com.example.demo.Entities.Experiment;

import javax.persistence.*;

@Entity
@Table(name = "grading_tasks")
public class GradingTask {
    @Id
    private int grading_task_id;
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
