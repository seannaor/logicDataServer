package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import org.json.simple.JSONObject;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tagging_results")
public class TaggingResult extends Result {
    @OneToMany(mappedBy = "taggingResult")
    private List<RequirementTag> tags;

    public TaggingResult() { }

    public TaggingResult(TaggingStage taggingStage, Participant participant) {
        super(taggingStage, participant);
        this.tags = new ArrayList<>();
    }

    public void addTag(RequirementTag tag){
        if(!tags.contains(tag))
            tags.add(tag);
    }

    public List<RequirementTag> getTags() {
        return tags;
    }

    public void setTags(List<RequirementTag> tags) {
        this.tags = tags;
    }

    @Override
    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        json.put("source stage","tagging");
        return json;
    }
}
