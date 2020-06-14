package com.example.demo.UnitTests.StagesUnitTests;

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

public class RequirementTest {

    private TaggingStage taggingStage;
    private Requirement requirement;
    private Participant participant;

    @BeforeEach
    public void init() {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        CodeStage codeStage = new CodeStage("make me hello world program", "//write code here", List.of("a requirement"), "JAVA");
        exp.addStage(codeStage);
        taggingStage = new TaggingStage(codeStage, exp);
        participant = new Experimentee("a@a.a", exp).getParticipant();
        requirement = codeStage.getRequirements().get(0);
    }

    @Test
    public void tagTest() {
        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);

        RequirementTag tag = requirement.tag(tag1);

        Assert.assertEquals(tag.getRequirement(), requirement);
    }
}
