package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stages")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Stage {

    @Embeddable
    public static class StageID implements Serializable {
        private int stageIndex;
        private int experimentId;

        public StageID() {
        }

        public StageID(int stageIndex) {
            this.stageIndex = stageIndex;
        }

        public StageID(int experimentId, int stageIndex) {
            this.experimentId = experimentId;
            this.stageIndex = stageIndex;
        }
    }

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "stageIndex", column = @Column(name = "stage_index")),
            @AttributeOverride(name = "experimentId", column = @Column(name = "experiment_id"))
    })
    private StageID stageID;

    @MapsId("experimentId")
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    public Stage() {
    }

    public Stage(Experiment experiment) {
        this.experiment = experiment;
        this.stageID = new StageID(experiment.getExperimentId(), experiment.getStages().size());
        experiment.addStage(this);
    }

    public Stage(Experiment experiment, int stage_index) {
//        this.stage_id = new StageID(experiment.getExperiment_id(), stage_index);
        this.stageID = new StageID(experiment.getExperimentId(), stage_index);
        this.experiment = experiment;
        experiment.addStage(this);
    }

    public StageID getStageID() {
        return stageID;
    }

    public void setStageID(StageID stageID) {
        this.stageID = stageID;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public abstract JSONObject getJson();

    public abstract String getType();



    public CodeResult fillCode(JSONObject data) throws FormatException {
        throw new FormatException("code stage answers");
    }

    public List<Answer> fillQuestionnaire(JSONObject data) throws FormatException, ParseException {
        throw new FormatException("questionnaire stage answers");
    }

    public List<RequirementTag> fillTagging(JSONObject data) throws FormatException {
        throw new FormatException("tagging stage answers");
    }

    public void fillInfo(JSONObject data)throws FormatException {
        throw new FormatException("info stage");
    }
}