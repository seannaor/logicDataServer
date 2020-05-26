package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.*;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private int participantId;
    @Column(name = "curr_stage")
    private int currStage;
    @Column(name = "is_done")
    private boolean isDone;
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;
    @OneToMany(mappedBy = "participant", fetch = FetchType.EAGER)
    private List<Result> results;

    public Participant() {
    }

    public Participant(Experiment experiment) {
        this.experiment = experiment;
        experiment.addParticipant(this);
        isDone = false;
        currStage = 0;
        this.results = new ArrayList<>();
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public int getParticipantId() {
        return participantId;
    }

    public Stage getCurrStage() throws ExpEndException, NotExistException {
        if (isDone) throw new ExpEndException();
        return experiment.getStage(currStage);
    }

    public Stage getNextStage() throws ExpEndException, NotExistException {
        advanceStage();
        if (isDone) throw new ExpEndException();
        return getCurrStage();
    }

    public Stage getStage(int idx) throws NotInReachException, NotExistException {
        if (currStage < idx) throw new NotInReachException("stage " + idx);
        return this.experiment.getStage(idx);
    }

    public Result getResult(int idx) throws NotInReachException {
        if (currStage < idx) throw new NotInReachException("result of stage " + idx);
        for (Result result : this.results) {
            if(result.getStage().getStageID().getStageIndex() == idx) {
                return result;
            }
        }
        return null;
    }

    private void advanceStage() {
        currStage++;
        if (currStage >= experiment.getStages().size())
            isDone = true;
    }

    public int getCurrStageIdx(){
        return this.currStage;
    }

    public boolean isDone() {
        return isDone;
    }

    public Result fillInStage(Map<String,Object> data) throws ExpEndException, FormatException, ParseException, NotInReachException, NotExistException {
        Stage curr = getCurrStage();
        String type = (String) data.getOrDefault("stageType", "no stage stated");
        switch (type) {
            case "code":
                CodeResult codeResult = curr.fillCode(data,this);
                this.results.add(codeResult);
                return codeResult;
            case "tagging":
                TaggingResult taggingResult = curr.fillTagging(data,this);
                this.results.add(taggingResult);
                return taggingResult;
            case "questionnaire":
                QuestionnaireResult questionnaireResult = curr.fillQuestionnaire(data,this);
                this.results.add(questionnaireResult);
                return questionnaireResult;
            default:
                throw new FormatException(curr.getType(), type);
        }
    }

    public Result getResultsOf(Stage visible) throws FormatException {
        for (Result result : results) {
            if (result.getStage().getStageID().equals(visible.getStageID()))
                return result;
        }
        throw new FormatException("code|questionnaire|tagging", visible.getType());
    }
}
