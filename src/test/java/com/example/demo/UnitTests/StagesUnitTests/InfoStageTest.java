package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import com.example.demo.Utils;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class InfoStageTest {
    private InfoStage infoStage;

    @BeforeEach
    private void init() {
        infoStage = new InfoStage("hello");
    }

    @Test
    public void setterGetter() {
        infoStage.setInfo("goodBye");
        Assert.assertEquals("goodBye", infoStage.getInfo());
    }

    @Test
    public void getAsMapTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", infoStage.getInfo());
        Assert.assertEquals(map, infoStage.getAsMap());
    }

    @Test
    public void getTypeTest() {
        Assert.assertEquals("info", infoStage.getType());
    }

    @Test
    public void fillDifferentTypesTest() throws ParseException, NotExistException, NotInReachException {
        // fails because infoStage can not be filled as a code stage
        try {
            infoStage.fillCode(new HashMap<>(),null);
            Assert.fail();
        } catch (FormatException ignored) {
        }

        // fails because infoStage can not be filled as a questionnaire stage
        try {
            infoStage.fillQuestionnaire(new HashMap<>(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }

        // fails because infoStage can not be filled as a tag stage
        try {
            infoStage.fillTagging(new HashMap<>(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }

        try {
            infoStage.fillInfo(new Object(), null);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void buildFromJson() throws FormatException {
        Stage stage = Stage.parseStage(Utils.getStumpInfoStage(), null);
        Assert.assertEquals("info", stage.getType());
    }
}
