package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "code_results")
public class CodeResult {
    @Embeddable
    public static class CodeResultID implements Serializable {
        private int participantId;
        private Stage.StageID stageID;
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
            @JoinColumn(name = "stage_index"),
            @JoinColumn(name = "experiment_id")
    })
    private CodeStage codeStage;

    public CodeResult() {
    }
}
