package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "results")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Result {
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

    public Result() {
    }

    public Result(Stage stage, Participant participant) {
        this.resultID = new ResultID(participant.getParticipantId(), stage.getStageID());
        this.participant = participant;
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ResultID getResultID() {
        return resultID;
    }

    public abstract Map<String, Object> getAsMap();

    @Embeddable
    public static class ResultID implements Serializable {
        @Column(name = "participant_id")
        private int participantId;
        private Stage.StageID stageID;

        public ResultID() {
        }

        public ResultID(int participantId, Stage.StageID stageID) {
            this.participantId = participantId;
            this.stageID = stageID;
        }
    }


}
