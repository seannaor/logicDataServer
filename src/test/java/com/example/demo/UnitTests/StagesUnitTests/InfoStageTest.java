package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoStageTest {
    private InfoStage infoStage;
    private Experimentee expee;

    @BeforeEach
    private void init() {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        infoStage = new InfoStage("hello");
        expee = new Experimentee("a@a.a", exp);
    }

    @Test
    public void getAsMapTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("text", infoStage.getInfo());
        Assert.assertEquals(map, infoStage.getAsMap());
    }
    @Test
    public void getTypeTest(){
        Assert.assertEquals("info", infoStage.getType());
    }
    @Test
    public void fillDifferentTypesTest() throws ParseException, NotExistException, NotInReachException {
        // fails because infoStage can not be filled as a code stage
        try {
            infoStage.fillCode(new HashMap<>(), expee.getParticipant());
            Assert.fail();
        } catch (FormatException e) {
        }
        // fails because infoStage can not be filled as a questionnaire stage
        try {
            infoStage.fillQuestionnaire(new HashMap<>(), expee.getParticipant());
            Assert.fail();
        } catch (FormatException e) {
        }
        // fails because infoStage can not be filled as a tag stage
        try {
            infoStage.fillTagging(new HashMap<>(), expee.getParticipant());
            Assert.fail();
        } catch (FormatException e) {
        }
        try {
            infoStage.fillInfo(new Object(), expee.getParticipant());
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void parseStageTest() throws FormatException {
        Stage stage = Stage.parseStage(Utils.getStumpInfoStage(), expee.getExperiment());
        Assert.assertTrue(stage instanceof InfoStage);
    }
}
