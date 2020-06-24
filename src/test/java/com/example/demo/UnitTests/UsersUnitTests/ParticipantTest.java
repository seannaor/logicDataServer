package com.example.demo.UnitTests.UsersUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
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

import static com.example.demo.Utils.buildParticipantTag;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParticipantTest {
    private Participant participant;
    private List<Map<String,Object>> stagesJson;

    @BeforeEach
    private void init() throws FormatException {
        Experiment exp = new Experiment("Experiment Name");
        stagesJson = Utils.buildStages();
        for (Map<String,Object> stageJ : stagesJson) {
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

        assertThrows(ExpEndException.class, () -> {
            participant.getNextStage();
        });
    }

    @Test
    public void getStageTest() throws NotInReachException, NotExistException, ExpEndException {
        int idx = 0;
        Assert.assertEquals(stagesJson.get(idx).get("type"), participant.getStage(idx).getType());
        idx++;

        int finalIdx = idx;
        assertThrows(NotInReachException.class, () -> {
            participant.getStage(finalIdx);
        });

        participant.getNextStage();
        participant.getNextStage();
        participant.getNextStage(); // after 3 stages

        assertThrows(ExpEndException.class, () -> {
            participant.getNextStage();
        });

        assertThrows(NotExistException.class, () -> {
            participant.getStage(stagesJson.size());
        });
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
        assertThrows(FormatException.class, () -> {
            participant.fillInStage(Map.of("answer231312312", List.of("a lot!", "22")));
        });
    }

    @Test
    public void fillQuestionnaireFailNoQuestionnaire() throws NotInReachException, NotExistException, ExpEndException, ParseException {
        // not in questionnaire stage
        assertThrows(FormatException.class, () -> {
            participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
        });
    }

    @Test
    public void fillCodeStageFailNoCode() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        // not in code stage
        assertThrows(FormatException.class, () -> {
            participant.fillInStage(Map.of("code", "return -1"));
        });
    }

    @Test
    public void fillCodeStageFailFormat() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        participant.getNextStage();
        participant.getNextStage();
        assertThrows(FormatException.class, () -> {
            participant.fillInStage(Map.of("code!1232132", "return -1"));
        });
    }

    @Test
    public void fillInCodeStageTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        participant.getNextStage();
        participant.getNextStage();
        CodeResult result = (CodeResult) participant.fillInStage(Map.of("code", "return -1"));
        Assert.assertEquals(result.getUserCode(), "return -1");
    }

    @Test
    public void fillTaggingStageFailNotTagging() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        Map<String,Object> ans = buildParticipantTag();
        assertThrows(FormatException.class, () -> {
            participant.fillInStage(Map.of("tagging", ans));
        });
    }

    @Test
    public void fillTaggingStageFailFormat() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        Map<String,Object>  ans = buildParticipantTag();
        participant.getNextStage();
        participant.getNextStage();
        participant.fillInStage(Map.of("code", "return 0;"));
        participant.getNextStage();
        assertThrows(FormatException.class, () -> {
            participant.fillInStage(Map.of("tagging!1232132", ans));
        });
    }

    @Test
    public void fillInTaggingStageTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        Map<String, Object> ans = buildParticipantTag();
        participant.getNextStage();
        participant.getNextStage();
        participant.fillInStage(Map.of("code", "return 0;"));
        participant.getNextStage();

        TaggingResult result = (TaggingResult) participant.fillInStage(ans);
        Assert.assertEquals(result.getTags().size(), 3);
    }

    @Test
    public void getResultTest() throws NotInReachException, NotExistException, ExpEndException, FormatException, ParseException {
        participant.getNextStage();
        assertThrows(NotInReachException.class, () -> {
            participant.getResult(2);
        });

        Assert.assertNull(participant.getResult(1));
        participant.fillInStage(Map.of("answers", List.of("a lot!", "22")));
        QuestionnaireResult result = (QuestionnaireResult) participant.getResult(1);
        Assert.assertEquals(result.getAnswers().size(), 2);
    }

}
