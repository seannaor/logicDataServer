package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Stages.Question;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AnswerUnitTests {

    private Answer openAns;
    private Question q1;
    private Question q2;


    @BeforeEach
    public void init() throws ParseException, FormatException {
        q1 = buildOpenQuestion();
        q2 = buildmultiChoiceQuestion();

        openAns = q1.answer("a lot");
    }

    @Test
    public void constructor() {
        String text = "this is my answer";
        Answer ans = new Answer(text, q1);
        Assert.assertEquals(q1, ans.getQuestion());
        Assert.assertEquals(text, ans.getAnswer());
    }

    @Test
    public void setGetText() {
        String ans = "open answer!";
        openAns.setAnswer(ans);
        Assert.assertEquals(ans, openAns.getAnswer());
    }

    @Test
    public void setGetQuestion() {
        openAns.setQuestion(q2);
        Assert.assertEquals(q2, openAns.getQuestion());
    }

    private Question buildOpenQuestion() {
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "open");
        JQuestion.put("question", "how much");
        return new Question(JQuestion.toString());
    }

    private Question buildmultiChoiceQuestion() {

        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "multiChoice");
        JQuestion.put("question", "who?");
        JQuestion.put("possibleAnswers", List.of("me", "you", "no one", "we both"));
        return new Question(JQuestion.toString());
    }
}
