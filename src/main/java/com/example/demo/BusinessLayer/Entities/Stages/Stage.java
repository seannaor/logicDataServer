package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
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

    public static Stage parseStage(Map<String,Object> stage, Experiment exp) throws FormatException {
        try {
            switch ((String) stage.get("type")) {
                case "info":
                    return new InfoStage((String) stage.get("info"));

                case "code":
                    return new CodeStage((String) stage.get("description"), (String) stage.get("template"),
                            (List<String>) stage.get("requirements"), (String) stage.get("language"));

                case "questionnaire":
                    return new QuestionnaireStage((List<JSONObject>) stage.get("questions"));

                case "tagging":
                    int codeIdx = (int) stage.get("codeIndex");
                    CodeStage codeStage = (CodeStage) exp.getStage(codeIdx);
                    return new TaggingStage(codeStage);
            }
        } catch (Exception ignore) {
            throw new FormatException("legal stage");
        }
        throw new FormatException("stage type");
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
        this.stageID = new StageID(experiment.getExperimentId(), experiment.getStages().size());
        experiment.addStage(this);
    }

    public abstract Map<String, Object> getAsMap();

    public abstract String getType();


    public CodeResult fillCode(Map<String, Object> data, CodeResult old) throws FormatException {
        throw new FormatException("code stage answers");
    }

    public QuestionnaireResult fillQuestionnaire(Map<String, Object> data, QuestionnaireResult old) throws FormatException, ParseException, NotInReachException, NotExistException {
        throw new FormatException("questionnaire stage answers");
    }

    public TaggingResult fillTagging(Map<String, Object> data,String userCode, TaggingResult old) throws FormatException {
        throw new FormatException("tagging stage answers");
    }

    public void fillInfo(Object data, Participant participant) throws FormatException {
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

        public StageID(int experimentId, int stageIndex) {
            this.experimentId = experimentId;
            this.stageIndex = stageIndex;
        }

        public int getStageIndex() {
            return stageIndex;
        }
    }
}
