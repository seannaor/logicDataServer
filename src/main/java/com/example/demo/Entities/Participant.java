package com.example.demo.Entities;

import javax.persistence.*;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private int participantId;
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    public Participant() {
    }

    public Participant(Experiment experiment) {
        this.experiment = experiment;
    }
}
