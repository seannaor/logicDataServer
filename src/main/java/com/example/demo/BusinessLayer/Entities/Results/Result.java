package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.hibernate.annotations.Columns;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "results")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Result {
    @Embeddable
    public static class ResultID implements Serializable {
        @Column(name = "participant_id")
        private int participantId;
        private Stage.StageID stageID;

        public ResultID() { }

        public ResultID(int participantId, Stage.StageID stageID) {
            this.participantId = participantId;
            this.stageID = stageID;
        }
    }
    @EmbeddedId
    private ResultID resultID;

    @MapsId("stageID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private Stage stage;

    @MapsId("participantId")
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    public Result() { }

    public Stage getStage() {
        return stage;
    }

    public Participant getParticipant() {
        return participant;
    }

    public ResultID getResultID() {
        return resultID;
    }

    public abstract Map<String, Object> getAsMap();

    public void setStageAndParticipant(Stage stage,Participant participant){
        this.stage = stage;
        this.participant = participant;
        this.resultID = new ResultID(participant.getParticipantId(), stage.getStageID());
    }

}
