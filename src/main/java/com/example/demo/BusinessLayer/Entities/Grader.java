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
}
