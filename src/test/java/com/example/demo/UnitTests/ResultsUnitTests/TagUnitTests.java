package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class TagUnitTests {

    private TaggingStage taggingStage;
    private Requirement requirement;
    private Participant participant;
    private RequirementTag tag;

    @BeforeEach
    public void init() {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        CodeStage codeStage = new CodeStage("make me hello world program", "//write code here", List.of("a requirement"), "JAVA");
        exp.addStage(codeStage);
        taggingStage = new TaggingStage(codeStage);
        exp.addStage(taggingStage);
        participant = new Experimentee("a@a.a", exp).getParticipant();
        requirement = codeStage.getRequirements().get(0);

        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);

        tag = requirement.tag(tag1);
        List<RequirementTag> tags = new LinkedList<>();
        new TaggingResult(taggingStage, participant).setTags(tags);
    }

    @Test
    public void constructor() {
        RequirementTag newTag = new RequirementTag(10, 2, requirement, new TaggingResult(taggingStage, participant));

        Assert.assertEquals(10, newTag.getStart());
        Assert.assertEquals(2, newTag.getLength());
        Assert.assertEquals(requirement, newTag.getRequirement());
    }

    @Test
    public void setterGetterTest() {
        tag.setLength(2);
        Assert.assertEquals(2, tag.getLength());

        tag.setStart(2);
        Assert.assertEquals(2, tag.getStart());
    }

}
