package com.example.demo.UnitTests.GradingTasksUnitTets;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraderToParticipantUnitTests {

    private GraderToParticipant graderToParticipant;
    private GradingTask gradingTask;
    private Experiment evaluate;
    private Experiment exp;

    @BeforeEach
    private void init() throws ExistException {
        exp = new Experiment("Experiment Name");
        evaluate = new Experiment("Evaluation");
        Experiment general = new Experiment("General");
        evaluate.setGradingTaskExp(true);
        gradingTask = new GradingTask("Grading Task", exp, general, evaluate);
        Grader grader = new Grader("grader@mail");
        GraderToGradingTask graderToGradingTask = new GraderToGradingTask(gradingTask, grader);
        Participant participant = new Participant(exp);
        graderToParticipant = new GraderToParticipant(graderToGradingTask, participant);
    }

    @Test
    public void graderToParticipant() {

        Assert.assertFalse(graderToParticipant.getGradingState());

        // maybe move to GraderToGradingTaskTests
        Participant graderParticipant = graderToParticipant.getGraderParticipant();
        Assert.assertEquals(evaluate, graderParticipant.getExperiment());

        GraderToGradingTask graderToGradingTask = graderToParticipant.getGraderToGradingTask();

        Assert.assertTrue(graderToGradingTask.getGraderToParticipants().contains(graderToParticipant));

    }

    @Test
    public void settersGettersTests() {
        Grader grader = new Grader("DiffGrader@mail");
        GraderToGradingTask graderToGradingTask = new GraderToGradingTask(gradingTask, grader);
        Participant participant = new Participant(exp);
        graderToParticipant.setExpeeParticipant(participant);
        graderToParticipant.setGraderToGradingTask(graderToGradingTask);

        Assert.assertEquals(graderToGradingTask, graderToParticipant.getGraderToGradingTask());
        Assert.assertEquals(participant, graderToParticipant.getExpeeParticipant());
    }
}
