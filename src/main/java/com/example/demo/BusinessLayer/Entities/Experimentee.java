package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;

import javax.persistence.*;

@Entity
@Table(name = "experimentees")
public class Experimentee {
    @Id
    @Column(name = "access_code")
    private String accessCode;
    @Column(name = "experimentee_email")
    private String experimenteeEmail;
    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    public Experimentee() {
    }

    public Experimentee(String accessCode, String experimenteeEmail, Experiment exp) {
        this.accessCode = accessCode;
        this.experimenteeEmail = experimenteeEmail;
        this.participant = new Participant(exp);
    }

    public Experimentee(String accessCode, String experimenteeEmail) {
        this.accessCode = accessCode;
        this.experimenteeEmail = experimenteeEmail;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String access_code) {
        this.accessCode = access_code;
    }

    public String getExperimenteeEmail() {
        return experimenteeEmail;
    }

    public void setExperimenteeEmail(String experimentee_email) {
        this.experimenteeEmail = experimentee_email;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Experiment getExperiment(){
        return participant.getExperiment();
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Stage getCurrStage() throws ExpEndException {
        return participant.getCurrStage();
    }

    public Stage getNextStage() throws ExpEndException {
        return participant.getNextStage();
    }

    public Stage getStage(int idx) throws NotInReachException{
        return participant.getStage(idx);
    }

    public ResultWrapper getResults(int idx) throws NotInReachException{
        return participant.getResults(idx);
    }
}
