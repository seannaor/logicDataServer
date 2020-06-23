package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TaggingResultUnitTests {

    private TaggingResult taggingResult;
    private RequirementTag tag;

    @BeforeEach
    public void init() {
        taggingResult = new TaggingResult();
        Requirement req = new Requirement("r1");
        tag = new RequirementTag(0, 10, req);
        taggingResult.addTag(tag);
    }

    @Test
    public void getMapTest() {
        Map<String, Object> map = taggingResult.getAsMap();
        Assert.assertTrue(map.containsKey("tags"));
        Assert.assertTrue(map.get("tags") instanceof List);
        Assert.assertEquals(1, ((List) map.get("tags")).size());
        Assert.assertEquals(tag.getAsMap(), ((List) map.get("tags")).get(0));

    }
}
