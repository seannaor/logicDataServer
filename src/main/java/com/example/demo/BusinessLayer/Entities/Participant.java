package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Results.*;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
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

    public Participant() {
//        isDone=false;
//        currStage=0;
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

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
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

    private void advanceStage() {
        currStage++;
        if (currStage >= experiment.getStages().size())
            isDone = true;
    }

    public boolean isDone() {
        return isDone;
    }

    public void fillInStage(JSONObject data) throws ExpEndException, FormatException, ParseException {
        Stage curr = getCurrStage();
        String type = (String) data.getOrDefault("stageType", "no stage stated");

        switch (type) {
            case "code":
                CodeResult codeResult = curr.fillCode(data);
                codeResult.setParticipant(this);
                this.codeResults.add(codeResult);
                return;
            case "Tagging":
                List<RequirementTag> requirementTags = curr.fillTagging(data);
                for (RequirementTag tag : requirementTags)
                    tag.setParticipant(this);

                this.requirementTags.addAll(requirementTags);
                return;
            case "questionnaire":
                List<Answer> answers = curr.fillQuestionnaire(data);
                for (Answer ans : answers)
                    ans.setParticipant(this);

                this.answers.addAll(answers);
                return;
            case "info":
                curr.fillInfo(data);
                return;
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
