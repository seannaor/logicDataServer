package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
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
    @OneToMany(mappedBy = "participant")
    private List<Result> results;
    @OneToMany(mappedBy = "participant")
    private List<GradersGTToParticipants> gradersGTToParticipants;

    public Participant() {
    }

    public Participant(Experiment experiment) {
        this.experiment = experiment;
        experiment.addParticipant(this);
        isDone = false;
        currStage = 0;
        this.results = new ArrayList<>();
        this.gradersGTToParticipants = new ArrayList<>();
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void addGradersGTToParticipants(GradersGTToParticipants g) {
        if (!this.gradersGTToParticipants.contains(g))
            this.gradersGTToParticipants.add(g);
    }

    public Stage getCurrStage() throws ExpEndException {
        if (isDone) throw new ExpEndException();
        return experiment.getStages().get(currStage);
    }

    public Stage getNextStage() throws ExpEndException {
        advanceStage();
        if (isDone) throw new ExpEndException();
        return getCurrStage();
    }

    public Stage getStage(int idx) throws NotInReachException {
        if (currStage < idx) throw new NotInReachException("stage " + idx);
        return this.experiment.getStages().get(idx);
    }

    public Result getResults(int idx) throws NotInReachException {
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

    public boolean isDone() {
        return isDone;
    }

    public Result fillInStage(JSONObject data) throws ExpEndException, FormatException, ParseException {
        //TODO: when we will have one list of results, change fillTagging and fillQuestionnaire to return the wrappers
        Stage curr = getCurrStage();
        String type = (String) data.getOrDefault("stageType", "no stage stated");

        switch (type) {
            case "code":
                CodeResult codeResult = curr.fillCode(data,this);
                this.results.add(codeResult);
                return codeResult;
            case "Tagging":
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

    public Result getResultsOf(Stage visible) throws FormatException, NotExistException {
//        switch (visible.getType()) {
//            case "code":
//                return getCodeIn(visible.getStageID());
//            case "questionnaire":
//                return getAnsIn(visible.getStageID());
//            case "tagging":
//                return getTagsIn(visible.getStageID());
//            default:
//                throw new FormatException("code|questionnaire|tagging", visible.getType());
//        }
        //TODO: shahar needs to check this
        for (Result result : results) {
            if (result.getStage().getStageID().equals(visible.getStageID()))
                return result;
        }
        throw new FormatException("code|questionnaire|tagging", visible.getType());
    }
}
