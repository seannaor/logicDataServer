package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
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
        Map<String, Object> map = new HashMap<>();
        Assert.assertEquals(map, taggingStage.getAsMap());
    }

    @Test
    public void getTypeTest() {
        Assert.assertEquals("tagging", taggingStage.getType());
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
    public void fillIn() throws NotInReachException, FormatException {
        JSONObject ans = new JSONObject();
        JSONObject tag = getTag();
        ans.put(0, tag);
        TaggingResult tr = taggingStage.fillTagging(Map.of("tagging", ans),"", null);
        Assert.assertEquals(1, tr.getTags().size());
    }

    @Test
    public void fillInFailNoRequirementTag() {
        JSONObject ans = new JSONObject();
        JSONObject tag = getTag();
        ans.put(1, tag);

        assertThrows(FormatException.class, () -> {
            taggingStage.fillTagging(Map.of("tagging", ans),"", null);
        });
    }

    @Test
    public void parseStageTest() throws FormatException {
        JSONObject JTagging = new JSONObject();
        JTagging.put("type", "tagging");
        JTagging.put("codeIndex", taggingStage.getCodeStage().getStageID().getStageIndex());
        Stage stage = Stage.parseStage(JTagging, exp);
        Assert.assertEquals("tagging", stage.getType());
    }

    private JSONObject getTag() {
        JSONObject tag = new JSONObject();
        tag.put("start_loc", 0);
        tag.put("length", 10);
        return tag;
    }
}
