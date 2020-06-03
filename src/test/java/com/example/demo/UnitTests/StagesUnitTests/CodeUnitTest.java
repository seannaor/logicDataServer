package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class CodeUnitTest {

    private CodeStage codeStage;
    private Participant participant;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        codeStage = new CodeStage("make me hello world program","//write code here","JAVA",exp);
        participant = new Participant(exp);
        participant.setParticipantId(10);
    }

    @Test
    public void settersGetters(){
        String desc = "no description",temp = "//comment",lang = "SIC";
        codeStage.setDescription(desc);
        codeStage.setTemplate(temp);
        codeStage.setLanguage(lang);

        Assert.assertEquals(desc,codeStage.getDescription());
        Assert.assertEquals(temp,codeStage.getTemplate());
        Assert.assertEquals(lang,codeStage.getLanguage());
    }

    @Test
    public void requirements(){
        Assert.assertEquals(0,codeStage.getRequirements().size());

        Requirement r1 = new Requirement(codeStage,"R1");
        Requirement r2 = new Requirement(codeStage,"R2");

        codeStage.addRequirement(r1);
        codeStage.addRequirement(r2);

        Assert.assertEquals(2,codeStage.getRequirements().size());

        Assert.assertEquals(r1,codeStage.getRequirements().get(0));
        Assert.assertEquals(r2,codeStage.getRequirements().get(1));

        codeStage.setRequirements(List.of(r2,r1));

        Assert.assertEquals(2,codeStage.getRequirements().size());

        Assert.assertEquals(r1,codeStage.getRequirements().get(1));
        Assert.assertEquals(r2,codeStage.getRequirements().get(0));
    }

    @Test
    public void fillIn(){
        String code = "x++;\nreturn x;";
        try{
            CodeResult res = codeStage.fillCode(code,participant);
            Assert.assertEquals(code,res.getUserCode());
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    public void getMap(){
        Map<String,Object> codeMap = codeStage.getAsMap();
        Assert.assertTrue(codeMap.containsKey("description"));
        Assert.assertTrue(codeMap.containsKey("template"));
        Assert.assertTrue(codeMap.containsKey("language"));
        Assert.assertTrue(codeMap.containsKey("requirements"));

        Assert.assertEquals(codeStage.getDescription(),codeMap.get("description"));
        Assert.assertEquals(codeStage.getTemplate(),codeMap.get("template"));
        Assert.assertEquals(codeStage.getLanguage(),codeMap.get("language"));
        Assert.assertEquals(codeStage.getRequirements().size(),((List)codeMap.get("requirements")).size());
    }
}
