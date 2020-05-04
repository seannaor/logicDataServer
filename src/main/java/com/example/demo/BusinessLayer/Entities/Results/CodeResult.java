package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "code_results")
public class CodeResult implements ResultWrapper {

    @Embeddable
    public static class CodeResultID implements Serializable {
        @Column(name = "participant_id")
        private int participantId;
        private Stage.StageID stageID;

        public CodeResultID() { }

        public CodeResultID(int participantId, Stage.StageID stageID) {
            this.participantId = participantId;
            this.stageID = stageID;
        }
    }

    @EmbeddedId
    private CodeResultID codeResultID;

    @MapsId("participantId")
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @MapsId("stageID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private CodeStage codeStage;

    @Lob
    @Column(name = "user_code")
    private String userCode;

    public CodeResult() { }

    public CodeResult(Participant participant, CodeStage codeStage, String userCode) {
        this.codeResultID = new CodeResultID(participant.getParticipantId(), codeStage.getStageID());
        this.participant = participant;
        this.codeStage = codeStage;
        this.userCode = userCode;
    }

    public CodeResultID getCodeResultID() {
        return codeResultID;
    }

    public Stage.StageID getCodeStageID(){return codeStage.getStageID();}

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setCodeStage(CodeStage codeStage) {
        this.codeStage = codeStage;
    }

    @Override
    public JSONObject getAsJson() {
        JSONObject JResult = codeStage.getJson();
        JResult.put("user code",userCode);
        return JResult;
    }
}
