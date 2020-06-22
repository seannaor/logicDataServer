package com.example.demo.UnitTests.UsersUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraderUnitTest {
    private Grader grader;
    private GradingTask gradingTask;

    @BeforeEach
    public void init() {
        Experiment exp = new Experiment("Experiment Name");
        Experiment evaluate = new Experiment("Evaluation");
        Experiment general = new Experiment("General");
        evaluate.setGradingTaskExp(true);
        gradingTask = new GradingTask("Grading Task", exp, general, evaluate);
        grader = new Grader("grader@mail");
        new GraderToGradingTask(gradingTask,grader);
    }

    @Test
    public void mailTest(){
        String mail = "mail";
        grader = new Grader(mail);

        Assert.assertEquals(mail,grader.getGraderEmail());

        mail = "different mail";
        grader.setGraderEmail(mail);
        Assert.assertEquals(mail,grader.getGraderEmail());

        Assert.assertEquals(0,grader.getAssignedGradingTasks().size());
    }

    @Test
    public void addTask(){
        Assert.assertEquals(gradingTask,grader.getAssignedGradingTasks().get(0).getGradingTask());
    }
}
