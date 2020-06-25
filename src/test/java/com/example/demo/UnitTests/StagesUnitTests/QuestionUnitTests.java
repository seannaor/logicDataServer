package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Stages.Question;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuestionUnitTests {
    private Question open;
    private Question multi;


    @BeforeEach
    private void init() {
        open = buildOpenQuestion();
        open.setQuestionIndex(0);
        multi = buildmultiChoiceQuestion();
        multi.setQuestionIndex(1);
    }

    @Test
    public void answer() throws ParseException, FormatException {
        Answer openAns = open.answer("a lot");
        Answer multiAns = multi.answer("me");
        Assert.assertEquals("a lot", openAns.getAnswer());
        Assert.assertEquals("me", multiAns.getAnswer());
    }

    @Test
    public void answerFailParse() {
        Question q = new Question();
        q.setQuestionJson("not a JSON");

        assertThrows(ParseException.class, () -> {
            q.answer("answer");
        });
    }

    @Test
    public void answerFailFormat() {
        Question q = new Question();
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "not a type");
        q.setQuestionJson(JQuestion.toString());

        assertThrows(FormatException.class, () -> {
            q.answer("answer");
        });
    }

    @Test
    public void ID() {
        Assert.assertEquals(0, open.getIndex());
        Assert.assertEquals(1, multi.getIndex());
    }

    @Test
    public void getJson() {
        JSONObject JOpen = open.getQuestionJson();
        JSONObject JMulti = multi.getQuestionJson();

        Assert.assertTrue(JOpen.containsKey("questionType"));
        Assert.assertTrue(JOpen.containsKey("question"));
        Assert.assertTrue(JMulti.containsKey("questionType"));
        Assert.assertTrue(JMulti.containsKey("question"));
        Assert.assertTrue(JMulti.containsKey("possibleAnswers"));

        Assert.assertEquals("open", JOpen.get("questionType"));
        Assert.assertEquals("how much", JOpen.get("question"));

        Assert.assertEquals("multiChoice", JMulti.get("questionType"));
        Assert.assertEquals("who?", JMulti.get("question"));
        Assert.assertEquals(4, ((List) JMulti.get("possibleAnswers")).size());
    }

    @Test
    public void getJsonFail() {
        Question q = new Question();
        q.setQuestionJson("not a JSON");
        Assert.assertEquals(new JSONObject().toString(), q.getQuestionJson().toString());
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