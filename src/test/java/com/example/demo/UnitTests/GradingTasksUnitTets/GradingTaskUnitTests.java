package com.example.demo.UnitTests.GradingTasksUnitTets;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GradingTaskUnitTests {

    private GradingTask gradingTask;

    @BeforeEach
    public void init() {
        Experiment exp = new Experiment("Experiment Name");
        Experiment evaluate = new Experiment("Evaluation");
        Experiment general = new Experiment("General");
        evaluate.setGradingTaskExp(true);
        gradingTask = new GradingTask("Grading Task", exp, general, evaluate);
    }

    @Test
    public void setStagesToCheckFailIllegalIdx() throws FormatException {
        assertThrows(NotExistException.class, () -> {
            gradingTask.setStagesByIdx(List.of(-1));
        });
    }

    @Test
    public void setStagesToCheckFailNotExistIdx() {
        assertThrows(NotExistException.class, () -> {
            gradingTask.setStagesByIdx(List.of(0));
        });
    }

    @Test
    public void setStagesToCheckFailNoResult() {
        gradingTask.getBaseExperiment().addStage(new InfoStage("info"));
        assertThrows(FormatException.class, () -> {
            gradingTask.setStagesByIdx(List.of(0));
        });
    }

    @Test
    public void setStagesToCheckFail() throws FormatException, NotExistException {

        Stage toCheck = new CodeStage("", "", new LinkedList<>(), "");
        gradingTask.getBaseExperiment().addStage(toCheck);
        gradingTask.setStagesByIdx(List.of(toCheck.getStageID().getStageIndex())); // good

        Assert.assertEquals(toCheck.getStageID().getStageIndex(), gradingTask.getStages().get(0).getStageID().getStageIndex());
        Assert.assertEquals(1, gradingTask.getStages().size());
    }

    @Test
    public void assignGrader() throws NotExistException {
        Grader grader = new Grader("grader@mail");
        GraderToGradingTask graderToGradingTask = new GraderToGradingTask(gradingTask, grader);

        int graders = gradingTask.getAssignedGradingTasks().size();
        gradingTask.addAssignedGradingTasks(graderToGradingTask);
        Assert.assertEquals(graders + 1, gradingTask.getAssignedGradingTasks().size());

        Assert.assertTrue(gradingTask.getAssignedGradingTasks().contains(graderToGradingTask));
        Assert.assertEquals(graderToGradingTask, gradingTask.getGraderToGradingTask(grader));
    }

    @Test
    public void getGraderToGradingTaskFailNoGrader() {
        Grader grader = new Grader("grader@mail");
        assertThrows(NotExistException.class, () -> {
            gradingTask.getGraderToGradingTask(grader);
        });
    }

    @Test
    public void getName() {
        Assert.assertEquals("Grading Task", gradingTask.getGradingTaskName());
    }
}
