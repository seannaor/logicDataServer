package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TagUnitTests {

    private Requirement requirement;
    private RequirementTag tag;

    @BeforeEach
    public void init() throws FormatException {
        requirement = new Requirement("a requirement");
        tag = requirement.tag(0,10);
    }

    @Test
    public void constructor() {
        RequirementTag newTag = new RequirementTag(10, 2, requirement);

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
