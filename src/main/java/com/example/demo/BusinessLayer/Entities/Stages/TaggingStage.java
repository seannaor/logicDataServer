package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<String,Object> getAsMap() {
        return Map.of();
    }

    @Override
    public String getType() {
        return "tagging";
    }

    @Override
    public TaggingResult fillTagging(Map<String,Object> data, Participant participant) throws FormatException, NotInReachException {
        TaggingResult taggingResult = (TaggingResult)participant.getResult(this.getStageID().getStageIndex());
        if(taggingResult == null) {
            taggingResult = new TaggingResult(this, participant);
        }
        JSONObject tags;
        try{
            tags = (JSONObject) data.get("tagging");
        }catch (Exception e) {
            throw new FormatException("tags list");
        }
        for (Requirement r : codeStage.getRequirements()) {
            int i = r.getIndex();
            if (!tags.containsKey(i))
                throw new FormatException("tag for requirement #" + i);

            r.tag((JSONObject) tags.get(i), taggingResult); //adds the new tag to the taggingResult automatically
        }
        return taggingResult;
    }
}
