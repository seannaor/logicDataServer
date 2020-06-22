package com.example.demo.UnitTests.UsersUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.example.demo.Utils.buildSimpleExp;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExperimenteeUnitTest {

    private Experimentee experimenteeOfExp;
    private Experimentee experimenteeOfNoExp;
    private Experiment exp;

    @BeforeEach
    public void init() throws FormatException {
        exp = new Experiment("experiment");
        for (JSONObject stageJ : buildSimpleExp(List.of("what is youer name?"))) {
            Stage s = Stage.parseStage(stageJ, exp);
            exp.addStage(s);
        }
        experimenteeOfExp = new Experimentee("mail@gmail", exp);
        experimenteeOfNoExp = new Experimentee("noExp@gmail");
    }

    @Test
    public void ID() {
        // null because no DB for this test
        Assert.assertNull(experimenteeOfExp.getAccessCode());
    }

    @Test
    public void getParticipantTest() {
        Assert.assertNotNull(experimenteeOfExp.getParticipant());
        // participant is the connection of experimentee and experiment
        Assert.assertNull(experimenteeOfNoExp.getParticipant());
    }


    @Test
    public void EMailTest() {
        String mail = "newMail@post";
        experimenteeOfNoExp.setExperimenteeEmail(mail);
        Assert.assertEquals(mail,experimenteeOfNoExp.getExperimenteeEmail());
    }

    @Test
    public void getExpTest() {
        Assert.assertNotNull(experimenteeOfExp.getExperiment());
    }

    @Test
    public void setParticipantTest() {
        Participant participant = new Participant();
        experimenteeOfNoExp.setParticipant(participant);
        Assert.assertEquals(participant,experimenteeOfNoExp.getParticipant());
    }

    @Test
    public void getCurrNextStageTest() throws NotExistException, ExpEndException {
        Stage curr = experimenteeOfExp.getCurrStage();
        Assert.assertEquals(0,experimenteeOfExp.getCurrStageIdx());
        Assert.assertEquals(exp.getStages().get(0),curr);
        curr = experimenteeOfExp.getNextStage();
        Assert.assertEquals(1,experimenteeOfExp.getCurrStageIdx());
        Assert.assertEquals(exp.getStages().get(1),curr);
    }

    @Test
    public void getStageTest() throws NotInReachException, NotExistException, ExpEndException {
        experimenteeOfExp.getNextStage();
        for (int i = 0; i < exp.getStages().size(); i++) {
            Assert.assertEquals(exp.getStages().get(i),experimenteeOfExp.getStage(i));
        }
    }

    @Test
    public void getResultTest() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {

        assertThrows(NotInReachException.class,()->{
            experimenteeOfExp.getResult(4);
        });

        Assert.assertNull(experimenteeOfExp.getResult(0));

        Result res = answerExp(experimenteeOfExp);
        Assert.assertNotNull(res);
        Assert.assertEquals(res,experimenteeOfExp.getResult(1));

    }

    // just the info and the questions
    private Result answerExp(Experimentee expee) throws NotExistException, ExpEndException, ParseException, FormatException, NotInReachException {
        expee.getNextStage();
        return expee.getParticipant().fillInStage(Map.of("answers",List.of("my name is S****r M***d")));
    }
}
