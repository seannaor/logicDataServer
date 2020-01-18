package com.example.demo;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "experiments")
public class Experiment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int experiment_id;
    private String experiment_name;
    @OneToMany(mappedBy = "experiment")
    private List<Stage> stages;

    public Experiment(String experiment_name) {
        this.experiment_name = experiment_name;
    }

    public Experiment() {
    }

    public int getExperiment_id() {
        return experiment_id;
    }

    public String getExperiment_name() {
        return experiment_name;
    }

    public boolean addStage(Stage stage) {
        stages.add(stage);
        return true;
    }
}
