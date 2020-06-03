package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class RequirementTest {
    @Test
    public void tagTest() {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        Requirement requirement = new Requirement(new CodeStage("make me hello world program","//write code here","JAVA",exp), "some requirement");
        Experimentee expee = new Experimentee("a@a.a", exp);
        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);
        TaggingStage ts = new TaggingStage(new CodeStage("make me hello world program","//write code here","JAVA",exp),exp);
        RequirementTag rt = requirement.tag(tag1, new TaggingResult(ts, expee.getParticipant()));
        Assert.assertEquals(rt.getRequirement(), requirement);
    }
}
