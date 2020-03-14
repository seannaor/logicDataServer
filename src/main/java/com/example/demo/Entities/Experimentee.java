package com.example.demo.Entities;

import javax.persistence.*;

@Entity
@Table(name = "experimentees")
public class Experimentee {
    @Id
    private String access_code;
    private String experimentee_email;
    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    public Experimentee() {
    }

    public Experimentee(String access_code, String experimentee_email) {
        this.access_code = access_code;
        this.experimentee_email = experimentee_email;
    }

    public String getAccess_code() {
        return access_code;
    }

    public void setAccess_code(String access_code) {
        this.access_code = access_code;
    }

    public String getExperimentee_email() {
        return experimentee_email;
    }

    public void setExperimentee_email(String experimentee_email) {
        this.experimentee_email = experimentee_email;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
