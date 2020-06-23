package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class QuestionnaireResultUnitTests {

    private QuestionnaireResult questionnaireResult = new QuestionnaireResult();

    @Test
    public void getMapTest() {
        Answer answer = new Answer();
        answer.setAnswer("this is my final answer");
        questionnaireResult.addAns(answer);

        Map<String, Object> map = questionnaireResult.getAsMap();
        Assert.assertTrue(map.containsKey("answers"));
        Assert.assertTrue(map.get("answers") instanceof List);
        Assert.assertEquals(1, ((List) map.get("answers")).size());
        Assert.assertEquals(answer.getAnswer(), ((List) map.get("answers")).get(0));
    }
}
