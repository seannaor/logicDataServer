package com.example.demo.UnitTests.UsersUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
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

import java.util.List;
import java.util.Map;

public class ParticipantTest {
    private Participant participant;
    private List<JSONObject> stagesJson;

    @BeforeEach
    private void init() throws FormatException {
        Experiment exp = new Experiment("Experiment Name");
        stagesJson = Utils.buildStages();
        for (JSONObject stageJ : stagesJson) {
            Stage s = Stage.parseStage(stageJ, exp);
            exp.addStage(s);
        }
        participant = new Participant(exp);
    }

    @Test
    public void getCurrNextStageTest() throws NotExistException, ExpEndException {
        Assert.assertEquals(stagesJson.get(0).get("type"), participant.getCurrStage().getType());
        for (int i = 1; i < stagesJson.size(); i++) {
            Assert.assertEquals(stagesJson.get(i).get("type"), participant.getNextStage().getType());
        }

        try {
            participant.getNextStage();
            Assert.fail();
        } catch (ExpEndException e) {
            Assert.assertTrue(participant.isDone());
        }
    }

    @Test
    public void getStageTest() throws NotInReachException, NotExistException, ExpEndException {
        int idx = 0;
        Assert.assertEquals(stagesJson.get(idx).get("type"),participant.getStage(idx).getType());
        idx++;
        try {
            participant.getStage(idx);
            Assert.fail();
        } catch (NotInReachException ignored) {}

        participant.getNextStage();participant.getNextStage();participant.getNextStage(); // after 3 stages

        try {
            participant.getNextStage();
            Assert.fail();
        } catch (ExpEndException e) {
            try {
                participant.getStage(stagesJson.size());
                Assert.fail();
            } catch (NotExistException ignored) {}
        }
    }

    @Test
    public void fillQuestionnaireTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        participant.getNextStage();
        QuestionnaireResult result = (QuestionnaireResult) participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
        Assert.assertEquals(result.getAnswers().size(), 2);
    }

    @Test
    public void fillQuestionnaireFailFormat() throws NotInReachException, NotExistException, ExpEndException, ParseException {
        participant.getNextStage();
        try {
            participant.fillInStage(Map.of("answer231312312", List.of("a lot!", "22")));
            Assert.fail();
        } catch (FormatException ignored) {}
    }

    @Test
    public void fillQuestionnaireFailNoQuestionnaire() throws NotInReachException, NotExistException, ExpEndException, ParseException {
        // not in questionnaire stage
        try {
            participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
            Assert.fail();
        } catch (FormatException ignored) {}
    }

    @Test
    public void fillCodeStageFailNoCode() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        // not in code stage
        try {
            participant.fillInStage(Map.of("code", "return -1"));
            Assert.fail();
        } catch (FormatException ignored) {}
    }

    @Test
    public void fillCodeStageFailFormat() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        participant.getNextStage();
        participant.getNextStage();

        // bad format
        try {
            participant.fillInStage(Map.of("code!1232132", "return -1"));
            Assert.fail();
        } catch (FormatException ignored) {
        }
    }

    @Test
    public void fillInCodeStageTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        participant.getNextStage();participant.getNextStage();
        CodeResult result = (CodeResult) participant.fillInStage(Map.of("code", "return -1"));
        Assert.assertEquals(result.getUserCode(), "return -1");
    }

    @Test
    public void fillTaggingStageFailNotTagging() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        JSONObject ans = buildParticipantTag();
        // not in tagging stage
        try {
            participant.fillInStage(Map.of("tagging", ans));
            Assert.fail();
        } catch (FormatException ignored) {}
    }

    @Test
    public void fillTaggingStageFailFormat() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        JSONObject ans = buildParticipantTag();
        participant.getNextStage();participant.getNextStage();participant.getNextStage();

        // bad format
        try {
            participant.fillInStage(Map.of("tagging!1232132", ans));
            Assert.fail();
        } catch (FormatException ignored) {
        }
    }

    @Test
    public void fillInTaggingStageTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        JSONObject ans = buildParticipantTag();
        participant.getNextStage();participant.getNextStage();participant.getNextStage();

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
        } catch (NotInReachException ignored) {}

        Assert.assertNull(participant.getResult(1));
        participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
        QuestionnaireResult result = (QuestionnaireResult) participant.getResult(1);
        Assert.assertEquals(result.getAnswers().size(), 2);
    }
}
