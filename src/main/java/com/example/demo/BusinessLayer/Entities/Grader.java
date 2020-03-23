package com.example.demo.BusinessLayer.Entities;

import javax.persistence.*;

@Entity
@Table(name = "graders")
public class Grader {
    @Id
    @Column(name = "grader_email")
    private String graderEmail;
    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    public Grader(String graderEmail, Experiment exp) {
        this.graderEmail = graderEmail;
        this.participant = new Participant(exp);
    }

    public String getGraderEmail() {
        return graderEmail;
    }

    public Experiment getExperiment(){return participant.getExperiment();}
}
