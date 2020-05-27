package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "stages")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Stage {

    @EmbeddedId
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
        this.stageID = new StageID(experiment.getExperimentId(), stage_index);
        this.experiment = experiment;
        experiment.addStage(this);
    }

    public static Stage parseStage(JSONObject stage, Experiment exp) throws FormatException {
        try {
            switch ((String) stage.get("type")) {
                case "info":
                    return new InfoStage((String) stage.get("info"), exp);

                case "code":
                    return new CodeStage((String) stage.get("description"), (String) stage.get("template"),
                            (List<String>) stage.get("requirements"), exp, (String) stage.get("language"));

                case "questionnaire":
                    return new QuestionnaireStage((List<JSONObject>) stage.get("questions"), exp);

                case "tagging":
                    int codeIdx = (int) stage.get("codeIndex");
                    CodeStage codeStage = (CodeStage) exp.getStage(codeIdx);
                    return new TaggingStage(codeStage, exp);
            }
        } catch (Exception ignore) {
            throw new FormatException("legal stage");
        }
        throw new FormatException("stage type");
    }

    public void setExp(Experiment experiment) {
        this.experiment = experiment;
        this.stageID = new StageID(experiment.getExperimentId(), experiment.getStages().size());
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

    public abstract Map<String, Object> getAsMap();

    public abstract String getType();


    public CodeResult fillCode(Map<String, Object> data, Participant participant) throws FormatException {
        throw new FormatException("code stage answers");
    }

    public QuestionnaireResult fillQuestionnaire(Map<String, Object> data, Participant participant) throws FormatException, ParseException, NotInReachException {
        throw new FormatException("questionnaire stage answers");
    }

    public TaggingResult fillTagging(Map<String, Object> data, Participant participant) throws FormatException, NotInReachException {
        throw new FormatException("tagging stage answers");
    }

    public void fillInfo(Map<String, Object> data, Participant participant) throws FormatException {
        throw new FormatException("info stage");
    }

    @Embeddable
    public static class StageID implements Serializable {
        @Column(name = "stage_index")
        private int stageIndex;
        @Column(name = "experiment_id")
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

        public int getStageIndex() {
            return stageIndex;
        }

        public void setStageIndex(int stageIndex) {
            this.stageIndex = stageIndex;
        }
    }
}