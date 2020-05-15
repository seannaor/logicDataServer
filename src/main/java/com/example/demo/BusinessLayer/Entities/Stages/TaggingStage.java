package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TagsWrapper;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public List<RequirementTag> fillTagging(JSONObject data, Participant participant) throws FormatException {
        TagsWrapper tagsWrapper = new TagsWrapper();
        List<RequirementTag> tags = new ArrayList<>();

        for (Requirement r : codeStage.getRequirements()) {
            int i = r.getIndex();
            if (!data.containsKey(i))
                throw new FormatException("tag for requirement #" + i);

            tags.add(r.tag((JSONObject) data.get(i),participant));
        }

        return tags;
    }
}
