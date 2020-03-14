package com.example.demo.Entities;

import javax.persistence.*;

@Entity
@Table(name = "graders")
public class Grader {
    @Id
    private String grader_email;
    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;
}
