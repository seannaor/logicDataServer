package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.simple.JSONObject;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tagging_results")
public class TaggingResult extends Result {
    @OneToMany(mappedBy = "taggingResult")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RequirementTag> tags;

    public TaggingResult() {
        this.tags = new ArrayList<>();
    }

    public void addTag(RequirementTag tag) {
        if (!tags.contains(tag))
            tags.add(tag);
    }

    public List<RequirementTag> getTags() {
        return tags;
    }

    public void setTags(List<RequirementTag> tags) {
        this.tags = tags;
    }

    @Override
    public void setStageAndParticipant(Stage stage, Participant participant) {
        super.setStageAndParticipant(stage, participant);
        for (RequirementTag tag : tags) {
            tag.setTaggingResult(this);
        }
    }

    @Override
    public Map<String, Object> getAsMap() {
        List<Map<String, Object>> JTags = new ArrayList<>();
        for(RequirementTag tag : this.getTags()){
            JTags.add(tag.getAsMap());
        }
        return Map.of("tags",JTags);
    }

}
