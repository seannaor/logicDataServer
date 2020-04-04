package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;

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
//    @OneToMany(mappedBy = "")
//    private List<Result> results;

    public Participant() {
    }

    public Participant(Experiment experiment) {
        this.experiment = experiment;
        experiment.addParticipant(this);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public Stage getCurrStage() {
        return null;
    }

    public Stage getNextStage() {
        return null;
    }

}
