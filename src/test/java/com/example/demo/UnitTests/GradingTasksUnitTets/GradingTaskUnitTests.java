package com.example.demo.UnitTests.GradingTasksUnitTets;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GradingTaskUnitTests {

    private GradingTask gradingTask;

    @BeforeEach
    public void init() throws NotExistException, FormatException {
        Experiment exp = new Experiment("Experiment Name");
        Experiment evaluate = new Experiment("Evaluation");
        Experiment general = new Experiment("General");
        evaluate.setGradingTaskExp(true);
        gradingTask = new GradingTask("Grading Task", exp, general, evaluate);
    }

    @Test
    public void setStagesToCheckFail() throws FormatException, NotExistException {
        try {
            //illegal stage idx
            gradingTask.setStagesByIdx(List.of(-1));
            Assert.fail();
        } catch (NotExistException ignored) {

        }

        try {
            //not exist stage idx
            gradingTask.setStagesByIdx(List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {

        }

        gradingTask.getBaseExperiment().addStage(new InfoStage("info"));

        try {
            //info stage - no result
            gradingTask.setStagesByIdx(List.of(0));
            Assert.fail();
        } catch (FormatException ignored) {

        }

        gradingTask.getBaseExperiment().addStage(new CodeStage("", "", "", gradingTask.getBaseExperiment()));
        gradingTask.setStagesByIdx(List.of(1)); // good

        Assert.assertEquals(2, gradingTask.getStages().get(0).getStageID().getStageIndex());
        Assert.assertEquals(1, gradingTask.getStages().size());
    }

    @Test
    public void assignGrader() {
        Grader grader = new Grader("grader@mail");
        GraderToGradingTask graderToGradingTask = new GraderToGradingTask(gradingTask, grader);

        int graders = gradingTask.getAssignedGradingTasks().size();
        gradingTask.addAssignedGradingTasks(graderToGradingTask);
        Assert.assertEquals(graders + 1, gradingTask.getAssignedGradingTasks().size());

        Assert.assertTrue(gradingTask.getAssignedGradingTasks().contains(graderToGradingTask));
    }
}
