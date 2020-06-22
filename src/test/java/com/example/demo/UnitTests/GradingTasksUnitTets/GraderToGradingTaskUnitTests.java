package com.example.demo.UnitTests.GradingTasksUnitTets;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Exceptions.*;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraderToGradingTaskUnitTests {

    private GradingTask gradingTask;
    private Experiment exp;
    private Grader grader;
    private GraderToGradingTask graderToGradingTask;
    private Experiment general;
    private GraderToParticipant graderToParticipant1;
    private Participant p2;

    @BeforeEach
    private void init() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException, ExistException {
        exp = new Experiment("Experiment Name");
        exp.addStage(new QuestionnaireStage());
        Experiment evaluate = new Experiment("Evaluation");
        general = new Experiment("General");
        evaluate.setGradingTaskExp(true);
        gradingTask = new GradingTask("Grading Task", exp, general, evaluate);
        gradingTask.setStagesByIdx(List.of(0));
        grader = new Grader("grader@mail");
        graderToGradingTask = new GraderToGradingTask(gradingTask, grader);
        Participant p1 = new Participant(exp);
        p1.setParticipantId(1);
        graderToParticipant1 = new GraderToParticipant(graderToGradingTask, p1);
        p2 = new Participant(exp);
        p2.setParticipantId(2);
        p2.fillInStage(Map.of("answers", List.of()));
        try {
            p2.getNextStage();
        } catch (ExpEndException ignored) {
        }
        graderToGradingTask.addParticipant(p2);
    }

    @Test
    public void initTest() {
        Assert.assertEquals(general, graderToGradingTask.getGeneralExpParticipant().getExperiment());
        Assert.assertTrue(gradingTask.getAssignedGradingTasks().contains(graderToGradingTask));
        Assert.assertTrue(grader.getAssignedGradingTasks().contains(graderToGradingTask));
    }

    @Test
    public void addGraderToExistParticipantFail(){
        assertThrows(ExistException.class, () -> {
            // trying to add GraderToParticipant that already there
            graderToGradingTask.addGraderToParticipant(graderToParticipant1);
        });
    }

    @Test
    public void addExistParticipantFail(){
        assertThrows(ExistException.class, () -> {
            // trying to add Participant that already there
            graderToGradingTask.addParticipant(p2);
        });
    }

    @Test
    public void addGraderToParticipant() throws ExistException {
        Participant p3 = new Participant(exp);
        p3.setParticipantId(3);
        graderToGradingTask.addParticipant(p3);

        List<Participant> participants = graderToGradingTask.getParticipants();
        Assert.assertTrue(participants.contains(p3));
    }

    @Test
    public void submitResultNoParticipant()  {
        assertThrows(NotExistException.class, () -> {
            //not exist user
            graderToGradingTask.submitResults(-1);
        });
    }

    @Test
    public void isSubmmitedNoParticipant()  {
        assertThrows(NotExistException.class, () -> {
            //not exist user
            graderToGradingTask.isSubmitted(-1);
        });
    }

    @Test
    public void submitResult() throws NotExistException {
        Assert.assertFalse(graderToGradingTask.isSubmitted(p2.getParticipantId()));
        graderToGradingTask.submitResults(p2.getParticipantId());
        Assert.assertTrue(graderToGradingTask.isSubmitted(p2.getParticipantId()));

        Assert.assertFalse(graderToGradingTask.isSubmitted(graderToParticipant1.getExpeeParticipant().getParticipantId()));
        graderToGradingTask.submitResults(graderToParticipant1.getExpeeParticipant().getParticipantId());
        Assert.assertTrue(graderToGradingTask.isSubmitted(graderToParticipant1.getExpeeParticipant().getParticipantId()));
    }

    @Test
    public void getParticipantNotExist(){
        assertThrows(NotExistException.class, () -> {
            graderToGradingTask.getExperimenteeParticipant(-1);
        });
    }

    @Test
    public void getParticipants() throws NotExistException {
        List<Participant> participants = graderToGradingTask.getParticipants();
        Assert.assertEquals(2, participants.size());

        Assert.assertTrue(participants.contains(p2));
        Assert.assertTrue(participants.contains(graderToParticipant1.getExpeeParticipant()));

        Assert.assertEquals(p2, graderToGradingTask.getExperimenteeParticipant(p2.getParticipantId()));
    }

    @Test
    public void getGraderParticipant() throws NotExistException {
        Assert.assertNotNull(graderToGradingTask.getGraderParticipant(p2.getParticipantId()));

        assertThrows(NotExistException.class, () -> {
            graderToGradingTask.getGraderParticipant(-1);
        });
    }

    @Test
    public void getResultsNotParticipant()  {
        assertThrows(NotExistException.class, () -> {
            //not exist user
            graderToGradingTask.getExpeeRes(-1);
        });
    }

    @Test
    public void getResultsNotFinished() throws NotInReachException, FormatException, NotExistException {
        assertThrows(NotInReachException.class, () -> {
            //didn't finish exp
            graderToGradingTask.getExpeeRes(graderToParticipant1.getExpeeParticipant().getParticipantId());
        });
    }

    @Test
    public void getResults() throws NotInReachException, FormatException, NotExistException {

        List<Result> results = graderToGradingTask.getExpeeRes(p2.getParticipantId());
        Assert.assertEquals(1, results.size());
        Assert.assertTrue((results.get(0) instanceof QuestionnaireResult));
        Assert.assertEquals(0, ((QuestionnaireResult) results.get(0)).getAnswers().size());
    }

    @Test
    public void setGetCode(){
        UUID id = new UUID(0,10);
        graderToGradingTask.setGraderAccessCode(id);
        assertEquals(id,graderToGradingTask.getGraderAccessCode());
    }
}
