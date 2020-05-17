package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "tagging_stages")
public class TaggingStage extends Stage {

    //TODO: i dropped NOT NULL on "appropriate_coding_stage_index" field to make it work, check if it can be bad

    @MapsId("stageID")
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "appropriate_coding_stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private CodeStage codeStage;

    public TaggingStage() {
    }

    public TaggingStage(CodeStage codeStage, Experiment experiment){
        super(experiment);
        this.codeStage = codeStage;
    }

    public TaggingStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public CodeStage getCodeStage() {
        return codeStage;
    }

    public void setCodeStage(CodeStage codeStage) {
        this.codeStage = codeStage;
    }

    public JSONObject getJson() {
        JSONObject jStage = new org.json.simple.JSONObject();
        jStage.put("type","tagging");
        return jStage;
    }

    @Override
    public String getType() {
        return "tagging";
    }

    @Override
    public TaggingResult fillTagging(JSONObject data, Participant participant) throws FormatException {
        TaggingResult taggingResult = new TaggingResult(this, participant);
        for (Requirement r : codeStage.getRequirements()) {
            int i = r.getIndex();
            if (!data.containsKey(i))
                throw new FormatException("tag for requirement #" + i);

            r.tag((JSONObject) data.get(i), taggingResult); //adds the new tag to the taggingResult automatically
        }
        return taggingResult;
    }
}
