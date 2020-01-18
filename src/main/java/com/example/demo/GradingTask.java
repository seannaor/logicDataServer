package com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "grading_tasks")
public class GradingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int grading_task_id;
    @ManyToOne
    @JoinColumns ({
        @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
    })
    private Stage stage;

    public GradingTask() {
    }

    public GradingTask (Stage stage) {
        this.stage = stage;
    }
}