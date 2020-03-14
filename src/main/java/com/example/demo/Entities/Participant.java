package com.example.demo.Entities;

import javax.persistence.*;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int participant_id;
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    public Participant() {
    }

    public Participant(Experiment experiment) {
        this.experiment = experiment;
    }
}
