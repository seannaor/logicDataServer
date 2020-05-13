package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
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
    private List<Answer> answers;
    @OneToMany(mappedBy = "participant")
    private List<CodeResult> codeResults;
    @OneToMany(mappedBy = "participant")
    private List<RequirementTag> requirementTags;
    @OneToMany(mappedBy = "participant")
    private List<GradersGTToParticipants> gradersGTToParticipants = new ArrayList<>();

    public Participant() {
    }

    public Participant(Experiment experiment) {
        this.experiment = experiment;
        experiment.addParticipant(this);
        isDone = false;
        currStage = 0;
        this.answers = new ArrayList<>();
        this.codeResults = new ArrayList<>();
        this.requirementTags = new ArrayList<>();
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

    public ResultWrapper getResults(int idx) throws NotInReachException {
        if (currStage < idx) throw new NotInReachException("result of stage " + idx);
        //TODO: when we will have one list of results, get results of stage idx.
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

    public ResultWrapper fillInStage(JSONObject data) throws ExpEndException, FormatException, ParseException {
        Stage curr = getCurrStage();
        String type = (String) data.getOrDefault("stageType", "no stage stated");

        switch (type) {
            case "code":
                CodeResult codeResult = curr.fillCode(data);
                codeResult.setParticipant(this);
                this.codeResults.add(codeResult);
                return codeResult;
            case "Tagging":
                TagsWrapper tags = new TagsWrapper();
                List<RequirementTag> requirementTags = curr.fillTagging(data);
                for (RequirementTag tag : requirementTags) {
                    tag.setParticipant(this);
                    tags.addTag(tag);
                }
                this.requirementTags.addAll(requirementTags);

                return tags;
            case "questionnaire":
                AnswersWrapper answersWrapper = new AnswersWrapper();
                List<Answer> answers = curr.fillQuestionnaire(data);
                for (Answer ans : answers) {
                    ans.setParticipant(this);
                    answersWrapper.addAns(ans);
                }
                this.answers.addAll(answers);
                return answersWrapper;
            default:
                throw new FormatException(curr.getType(), type);
        }
    }

    public ResultWrapper getResultsOf(Stage visible) throws FormatException, NotExistException {
        switch (visible.getType()) {
            case "code":
                return getCodeIn(visible.getStageID());
            case "questionnaire":
                return getAnsIn(visible.getStageID());
            case "tagging":
                return getTagsIn(visible.getStageID());
            default:
                throw new FormatException("code|questionnaire|tagging", visible.getType());
        }
    }

    private ResultWrapper getCodeIn(Stage.StageID id) throws NotExistException {
        for (CodeResult code : codeResults) {
            if (code.getCodeStageID().equals(id))
                return code;
        }
        throw new NotExistException("stage", id.toString());
    }

    private ResultWrapper getAnsIn(Stage.StageID id) {
        AnswersWrapper answers = new AnswersWrapper();
        for (Answer ans : this.answers) {
            if (ans.getStageID().equals(id))
                answers.addAns(ans);
        }
        return answers;
    }

    private ResultWrapper getTagsIn(Stage.StageID id) {
        TagsWrapper tags = new TagsWrapper();
        for (RequirementTag tag : requirementTags) {
            if (tag.getStageID().equals(id))
                tags.addTag(tag);
        }
        return tags;
    }
}
