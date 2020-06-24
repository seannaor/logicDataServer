package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;

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

    public TaggingStage(CodeStage codeStage) {
        setCodeStage(codeStage);
    }

    public CodeStage getCodeStage() {
        return codeStage;
    }

    public void setCodeStage(CodeStage codeStage) {
        this.codeStage = codeStage;
    }

    @Override
    public Map<String, Object> getAsMap() {
        return Map.of(
                "type", getType(),
                "stage", Map.of(
                        "codeStageIndex", this.codeStage.getStageID().getStageIndex() + 1)
        );
    }

    @Override
    public String getType() {
        return "tag";
    }

    // if old is null, new TaggingResult will be created, else, old will be chanced
    //TODO: validate old actually change
    @Override
    public TaggingResult fillTagging(Map<String, Object> data, String userCode, TaggingResult old) throws FormatException {
        if (old == null) {
            old = new TaggingResult();
        }
        List<List<Map<String, Object>>> JTags = validate(data);
        List<RequirementTag> tags = new LinkedList<>();
        List<Requirement> requirements = codeStage.getRequirements();
        String[] userCodeRows = userCode.split("\n");

        for (int i = 0; i < requirements.size(); i++) {
            Requirement r = requirements.get(i);

            for (Map<String, Object> SubTag : JTags.get(i)) {
                int startCharLoc = getCharLoc((Map) SubTag.get("from"), userCodeRows);
                int endCharLoc = getCharLoc((Map) SubTag.get("to"), userCodeRows) - 1;

                RequirementTag tag = r.tag(startCharLoc, endCharLoc - startCharLoc);
                tag.setCodeStageIdx(this.codeStage.getStageID().getStageIndex());
                tags.add(tag);
            }
        }
        old.setTags(tags);
        return old; // old is actually new now :)
    }

    private int getCharLoc(Map<String, Object> jTag, String[] userCodeRows) throws FormatException {
        int rowI = (int) jTag.get("row") - 1, colI = (int) jTag.get("col");
        int loc = colI;
        if (userCodeRows.length < rowI)
            throw new FormatException("row index smaller than the user code");
        String row = userCodeRows[0];
        for (int i = 0; i < userCodeRows.length && i < rowI; i++) {
            row = userCodeRows[i];
            loc += row.length();
        }
        if (row.length() < colI)
            throw new FormatException("col index smaller than the row length");
        return loc;
    }

    private List<List<Map<String, Object>>> validate(Map<String, Object> data) throws FormatException {
        List<List<Map<String, Object>>> tags;
        try {
            tags = (List<List<Map<String, Object>>>) data.get("tags");
            if (tags != null) return tags;
        } catch (Exception ignored) {
        }
        throw new FormatException("tags list");
    }
}


