package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.Utils.getStumpCodeStage;

public class CodeUnitTest {

    private CodeStage codeStage;

    @BeforeEach
    private void init() {
        codeStage = new CodeStage("make me hello world program", "//write code here", List.of("THE REQUIREMENT"), "JAVA");
    }

    @Test
    public void settersGetters() {
        String desc = "no description", temp = "//comment", lang = "SIC";
        codeStage.setDescription(desc);
        codeStage.setTemplate(temp);
        codeStage.setLanguage(lang);

        Assert.assertEquals(desc, codeStage.getDescription());
        Assert.assertEquals(temp, codeStage.getTemplate());
        Assert.assertEquals(lang, codeStage.getLanguage());
    }

    @Test
    public void requirements() {
        codeStage.setRequirements(new ArrayList<>());
        Assert.assertEquals(0, codeStage.getRequirements().size());

        Requirement r1 = new Requirement("R1");
        Requirement r2 = new Requirement( "R2");

        codeStage.addRequirement(r1);
        codeStage.addRequirement(r2);

        Assert.assertEquals(2, codeStage.getRequirements().size());

        Assert.assertEquals(r1, codeStage.getRequirements().get(0));
        Assert.assertEquals(r2, codeStage.getRequirements().get(1));

        codeStage.setRequirements(List.of(r2, r1));

        Assert.assertEquals(2, codeStage.getRequirements().size());

        Assert.assertEquals(r1, codeStage.getRequirements().get(1));
        Assert.assertEquals(r2, codeStage.getRequirements().get(0));
    }

    @Test
    public void fillIn() {
        String code = "x++;\nreturn x;";
        try {
            CodeResult res = codeStage.fillCode(Map.of("code", code), null);
            Assert.assertEquals(code, res.getUserCode());
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    public void getMap() {
        Map<String, Object> codeMap = codeStage.getAsMap();
        Assert.assertTrue(codeMap.containsKey("description"));
        Assert.assertTrue(codeMap.containsKey("template"));
        Assert.assertTrue(codeMap.containsKey("language"));
        Assert.assertTrue(codeMap.containsKey("requirements"));

        Assert.assertEquals(codeStage.getDescription(), codeMap.get("description"));
        Assert.assertEquals(codeStage.getTemplate(), codeMap.get("template"));
        Assert.assertEquals(codeStage.getLanguage(), codeMap.get("language"));
        Assert.assertEquals(codeStage.getRequirements().size(), ((List) codeMap.get("requirements")).size());
    }

    @Test
    public void buildFromJson() throws FormatException {
        Stage stage = Stage.parseStage(getStumpCodeStage(), null);
        Assert.assertEquals("code", stage.getType());
    }

    @Test
    public void fillDifferentTypesTest() throws ParseException, NotExistException, NotInReachException {
        // fails because infoStage can not be filled as a code stage
        try {
            codeStage.fillCode(Map.of("code", "hello world"), null);

        } catch (FormatException ignored) {
            Assert.fail();
        }

        // fails because infoStage can not be filled as a questionnaire stage
        try {
            codeStage.fillQuestionnaire(new HashMap<>(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }

        // fails because infoStage can not be filled as a tag stage
        try {
            codeStage.fillTagging(new HashMap<>(), null);
            Assert.fail();
        } catch (FormatException ignored) {
        }

        try {
            codeStage.fillInfo(new Object(), null);
            Assert.fail();
        } catch (Exception ignored) {
        }
    }
}
