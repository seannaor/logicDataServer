package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.LinkedList;
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

    // TODO: remove Experiment form constructor or all constructor
    public TaggingStage(CodeStage codeStage, Experiment experiment){
        super(experiment);
        this.codeStage = codeStage;
    }

    public TaggingStage(CodeStage codeStage){
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

    // if old is null, new TaggingResult will be created, else, old will be chanced
    //TODO: validate old actually change
    @Override
    public TaggingResult fillTagging(Map<String,Object> data, TaggingResult old) throws FormatException, NotInReachException {
        if(old == null) {
            old = new TaggingResult();
        }
        JSONObject JTags = validate(data);
        List<RequirementTag> tags = new LinkedList<>();

        for (Requirement r : codeStage.getRequirements()) {
            int i = r.getIndex();
            if (!JTags.containsKey(i))
                throw new FormatException("tag for requirement #" + i);

            RequirementTag tag =  r.tag((JSONObject) JTags.get(i));
            tag.setCodeStageIdx(this.codeStage.getStageID().getStageIndex());
            tags.add(tag);
        }
        old.setTags(tags);
        return old; // old is actually new now :)
    }

    private JSONObject validate(Map<String,Object> data) throws FormatException {
        JSONObject tags;
        try{
            tags = (JSONObject) data.get("tagging");
            if(tags != null) return tags;
        }catch (Exception ignored) {}
        throw new FormatException("tags list");
    }
}
