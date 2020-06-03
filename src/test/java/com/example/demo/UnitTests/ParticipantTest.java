package com.example.demo.UnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;

import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.*;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticipantTest {
    private Participant participant;

    @BeforeEach
    private void init() throws FormatException {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        List<JSONObject> stagesJson = Utils.buildStages();
        List<Stage> stages = new ArrayList<>();
        for(JSONObject stageJ : stagesJson) {
            stages.add(Stage.parseStage(stageJ,exp));
        }
        participant = new Participant(exp);
    }

    @Test
    public void getCurrGetNextTest() throws NotExistException, ExpEndException {
        Assert.assertTrue(participant.getCurrStage() instanceof InfoStage);
        Assert.assertTrue(participant.getNextStage() instanceof QuestionnaireStage);
        Assert.assertTrue(participant.getNextStage() instanceof CodeStage);
        Assert.assertTrue(participant.getNextStage() instanceof TaggingStage);
        try {
            participant.getNextStage();
            Assert.fail();
        } catch (ExpEndException e) {
            Assert.assertTrue(participant.isDone());
        }
    }
    @Test
    public void getStageTest() throws NotInReachException, NotExistException, ExpEndException {
        Assert.assertTrue(participant.getStage(0) instanceof InfoStage);
        try {
            participant.getStage(1);
            Assert.fail();
        } catch (NotInReachException e) { }
        participant.getNextStage();
        Assert.assertTrue(participant.getStage(1) instanceof QuestionnaireStage);
        participant.getNextStage();
        participant.getNextStage();
        try {
            participant.getNextStage();
            Assert.fail();
        } catch (ExpEndException e) {
            try {
                participant.getStage(4);
                Assert.fail();
            } catch (NotExistException ne) {
            }
        }
    }

    @Test
    public void fillInStageQuestionnaireTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        // not in questionnaire stage
        try {
            participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
            Assert.fail();
        } catch(FormatException fe) { }
        participant.getNextStage();
        // bad format
        try {
            participant.fillInStage(Map.of("answer231312312", List.of("a lot!", "22")));
            Assert.fail();
        } catch(FormatException fe) { }
        QuestionnaireResult result = (QuestionnaireResult) participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
        Assert.assertEquals(result.getAnswers().size(),  2);
        // not in questionnaire stage
        participant.getNextStage();
        try {
            participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
            Assert.fail();
        } catch(FormatException fe) { }
    }

    @Test
    public void fillInCodeStageTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        // not in code stage
        try {
            participant.fillInStage(Map.of("code","return -1"));
            Assert.fail();
        } catch(FormatException fe) { }
        participant.getNextStage();
        participant.getNextStage();
        // bad format
        try {
            participant.fillInStage(Map.of("code!1232132", "return -1"));
            Assert.fail();
        } catch(FormatException fe) { }
        CodeResult result = (CodeResult) participant.fillInStage(Map.of("code", "return -1"));
        Assert.assertEquals(result.getUserCode(), "return -1");
        // not in code stage
        participant.getNextStage();
        try {
            participant.fillInStage(Map.of("code", "return -1"));
            Assert.fail();
        } catch(FormatException fe) { }
    }

    @Test
    public void fillInTaggingStageTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        JSONObject ans = buildParticipantTag();
        // not in tagging stage
        try {
            participant.fillInStage(Map.of("tagging", ans));
            Assert.fail();
        } catch(FormatException fe) { }
        participant.getNextStage();
        participant.getNextStage();
        participant.getNextStage();
        // bad format
        try {
            participant.fillInStage(Map.of("tagging!1232132", ans));
            Assert.fail();
        } catch(FormatException fe) { }
        TaggingResult result = (TaggingResult) participant.fillInStage(Map.of("tagging", ans));
        Assert.assertEquals(result.getTags().size(), 3);
    }

    private JSONObject buildParticipantTag() {
        JSONObject ans = new JSONObject();
        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);
        ans.put(0, tag1);
        JSONObject tag2 = new JSONObject();
        tag2.put("start_loc", 0);
        tag2.put("length", 10);
        ans.put(1, tag2);
        JSONObject tag3 = new JSONObject();
        tag3.put("start_loc", 0);
        tag3.put("length", 10);
        ans.put(2, tag3);
        return ans;
    }

    @Test
    public void getResultTest() throws NotInReachException, NotExistException, ExpEndException, FormatException, ParseException {
        participant.getNextStage();
        try {
            participant.getResult(2);
            Assert.fail();
        } catch (NotInReachException e) { }
        Assert.assertEquals(participant.getResult(1), null);
        participant.fillInStage(Map.of("answers",List.of("a lot!","22")));
        QuestionnaireResult result = (QuestionnaireResult) participant.getResult(1);
        Assert.assertEquals(result.getAnswers().size(),  2);
    }
}
