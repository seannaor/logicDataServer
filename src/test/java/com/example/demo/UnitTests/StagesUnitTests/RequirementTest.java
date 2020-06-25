package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RequirementTest {

    private Requirement requirement;

    @BeforeEach
    public void init() {
        requirement = new Requirement("a requirement");
    }

    @Test
    public void tagTest() throws FormatException {
        RequirementTag tag = requirement.tag(0, 10);
        Assert.assertEquals(tag.getRequirement(), requirement);
    }

    @Test
    public void setGetTextTest() {
        String reqText = "different requirement";
        requirement.setText(reqText);
        Assert.assertEquals(reqText, requirement.getText());
    }
}
