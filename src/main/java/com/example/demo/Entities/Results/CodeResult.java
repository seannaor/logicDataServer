package com.example.demo.Entities.Results;


import com.example.demo.Entities.Participant;
import com.example.demo.Entities.Stages.CodeStage;
import com.example.demo.Entities.Stages.Stage;

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
