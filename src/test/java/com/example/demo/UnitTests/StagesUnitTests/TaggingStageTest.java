package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void fillCodeInsteadTagging() throws ParseException, NotExistException, NotInReachException {
        // fails because taggingStage can not be filled as a code stage
        try {
            taggingStage.fillCode(new HashMap<>(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }
    }

    @Test
    public void fillQuestionnaireInsteadTagging() throws ParseException, NotExistException, NotInReachException {
        // fails because taggingStage can not be filled as a questionnaire stage
        try {
            taggingStage.fillQuestionnaire(new HashMap<>(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }
    }

    @Test
    public void fillInfoInsteadTagging() throws ParseException, NotExistException, NotInReachException {
        // fails because taggingStage can not be filled as a info stage
        try {
            taggingStage.fillInfo(new Object(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }
    }

    @Test
    public void fillIn() throws NotExistException, ExpEndException, NotInReachException, FormatException {
        JSONObject ans = new JSONObject();
        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);
        ans.put(0, tag1);
        TaggingResult tr = taggingStage.fillTagging(Map.of("tagging", ans), null);
        Assert.assertEquals(1, tr.getTags().size());
    }

    @Test
    public void parseStageTest() throws FormatException {
        JSONObject JTagging = new JSONObject();
        JTagging.put("type", "tagging");
        JTagging.put("codeIndex", taggingStage.getCodeStage().getStageID().getStageIndex());
        Stage stage = Stage.parseStage(JTagging, exp);
        Assert.assertEquals("tagging", stage.getType());
    }
}
