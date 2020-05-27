package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "experimentees")
public class Experimentee {
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "access_code", columnDefinition = "BINARY(16)")
    @Id
    private UUID accessCode;
    @Column(name = "experimentee_email")
    private String experimenteeEmail;
    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    public Experimentee() {
    }

    public Experimentee(String experimenteeEmail, Experiment exp) {
        this.experimenteeEmail = experimenteeEmail;
        this.participant = new Participant(exp);
    }

    public Experimentee(String experimenteeEmail) {
        this.experimenteeEmail = experimenteeEmail;
    }

    public UUID getAccessCode() {
        return accessCode;
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

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Experiment getExperiment() {
        return participant.getExperiment();
    }

    public Stage getCurrStage() throws ExpEndException, NotExistException {
        return participant.getCurrStage();
    }

    public int getCurrStageIdx() {
        return participant.getCurrStageIdx();
    }

    public Stage getNextStage() throws ExpEndException, NotExistException {
        return participant.getNextStage();
    }

    public Stage getStage(int idx) throws NotInReachException, NotExistException {
        return participant.getStage(idx);
    }

    public Result getResult(int idx) throws NotInReachException {
        return participant.getResult(idx);
    }
}
