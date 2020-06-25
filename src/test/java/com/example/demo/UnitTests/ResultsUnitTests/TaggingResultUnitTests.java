package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
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

public class TaggingResultUnitTests {

    private TaggingResult taggingResult;
    private RequirementTag tag;

    @BeforeEach
    public void init() throws FormatException, NotExistException, ExpEndException, NotInReachException, ParseException {
        Experiment exp = new Experiment("Experiment Name");
        List<Map<String,Object>> stages = Utils.buildStages();
        for (Map<String,Object> stageJ : stages) {
            Stage s = Stage.parseStage(stageJ, exp);
            exp.addStage(s);
        }
        Participant participant = new Participant(exp);

        Map<String, Object> ans = buildParticipantTag();
        participant.getNextStage();
        participant.getNextStage();
        participant.fillInStage(Map.of("code", "return 0;"));
        participant.getNextStage();
        taggingResult = (TaggingResult) participant.fillInStage(ans);

        tag = taggingResult.getTags().get(0);

    }

    @Test
    public void getMapTest() {
        Map<String, Object> map = taggingResult.getAsMap();
        Assert.assertTrue(map.containsKey("tags"));
        Assert.assertEquals(3, ((List) map.get("tags")).size());

    }
}
