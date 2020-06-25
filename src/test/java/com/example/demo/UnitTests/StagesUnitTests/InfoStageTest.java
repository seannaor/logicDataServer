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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Assert.assertEquals(Map.of("type","info","stage",map), infoStage.getAsMap());
    }

    @Test
    public void getTypeTest() {
        Assert.assertEquals("info", infoStage.getType());
    }

    @Test
    public void fillDifferentTypesTest() throws ParseException, NotExistException, NotInReachException {
        assertThrows(FormatException.class, () -> {
            // fails because infoStage can not be filled as a code stage
            infoStage.fillCode(new HashMap<>(), null);
        });

        assertThrows(FormatException.class, () -> {
            // fails because infoStage can not be filled as a questionnaire stage
            infoStage.fillQuestionnaire(new HashMap<>(), null);
        });

        assertThrows(FormatException.class, () -> {
            // fails because infoStage can not be filled as a tag stage
            infoStage.fillTagging(new HashMap<>(),"", null);
        });


        infoStage.fillInfo(new Object(), null);
    }

    @Test
    public void buildFromJson() throws FormatException {
        Stage stage = Stage.parseStage(Utils.getStumpInfoMap(), null);
        Assert.assertEquals("info", stage.getType());
    }
}
