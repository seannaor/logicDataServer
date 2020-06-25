package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaggingStageTest {
    private TaggingStage taggingStage;
    private Experiment exp;


    @BeforeEach
    private void init() {
        exp = new Experiment("Experiment Name");
        List<String> requirements = new ArrayList<>();
        requirements.add("do that");
        CodeStage codeStage = new CodeStage("make me hello world program", "//write code here", requirements, "JAVA");
        exp.addStage(codeStage);
        taggingStage = new TaggingStage(codeStage);
        exp.addStage(taggingStage);
    }

    @Test
    public void getAsMapTest() {
        Map<String, Object> map = taggingStage.getAsMap();
        Assert.assertTrue(map.containsValue("tag"));
        map = (Map<String, Object>) map.get("stage");
        Assert.assertTrue(map.containsKey("codeStageIndex"));
    }

    @Test
    public void getTypeTest() {
        Assert.assertEquals("tag", taggingStage.getType());
    }

    @Test
    public void fillCodeInsteadTagging() {
        // fails because taggingStage can not be filled as a code stage
        assertThrows(FormatException.class, () -> {
            taggingStage.fillCode(new HashMap<>(), null);
        });
    }

    @Test
    public void fillQuestionnaireInsteadTagging() {
        // fails because taggingStage can not be filled as a questionnaire stage
        assertThrows(FormatException.class, () -> {
            taggingStage.fillQuestionnaire(new HashMap<>(), null);
        });
    }

    @Test
    public void fillInfoInsteadTagging() {
        // fails because taggingStage can not be filled as a info stage
        assertThrows(FormatException.class, () -> {
            taggingStage.fillInfo(new Object(), null);
        });
    }

    @Test
    public void fillIn() throws FormatException {
        Map<String, Object> fromTo = Map.of("from", Map.of("row", 1, "column", 0),
                "to", Map.of("row", 1, "column", 7));

        TaggingResult tr = taggingStage.fillTagging(Map.of("tags", List.of(List.of(fromTo, fromTo))), "return 0;", null);
        Assert.assertEquals(2, tr.getTags().size());
    }

    @Test
    public void fillInFailNoRequirementTag() {
        Map<String, Object> fromTo = Map.of("from", Map.of("row", 1, "column", 0),
                "to", Map.of("row", 1, "column", 10));

        assertThrows(FormatException.class, () -> {
            taggingStage.fillTagging(Map.of("tag", List.of(List.of(fromTo))), "", null);
        });
    }

    @Test
    public void parseStageTest() throws FormatException {
        Map<String, Object> stageMap = Map.of("codeStageIndex", taggingStage.getCodeStage().getStageID().getStageIndex() + 1);
        Stage stage = Stage.parseStage(Map.of("type", "tag", "stage", stageMap), exp);
        Assert.assertEquals("tag", stage.getType());
    }

    private JSONObject getTag() {
        JSONObject tag = new JSONObject();
        tag.put("start_loc", 0);
        tag.put("length", 10);
        return tag;
    }
}
