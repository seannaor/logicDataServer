package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Stages.Question;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class QuestionUnitTests {
    private Question open;
    private Question multi;
    private QuestionnaireStage stage;
    private Participant p;


    @BeforeEach
    private void init() {
        p = new Participant();
        p.setParticipantId(10);
        stage = new QuestionnaireStage();
        stage.setStageID(new Stage.StageID(1));
        open = buildOpenQuestion(stage);
        stage.addQuestion(open);
        multi = buildmultiChoiceQuestion(stage);
        stage.addQuestion(open);
    }

    @Test
    public void answer() throws ParseException, FormatException {
        QuestionnaireResult res = new QuestionnaireResult(stage, p);

        Answer openAns = open.answer("a lot", res);
        Answer multiAns = multi.answer("me", res);
        Assert.assertEquals("a lot", openAns.getAnswer());
        Assert.assertEquals("me", multiAns.getAnswer());
    }

    @Test
    public void answerFailFormat() throws ParseException, FormatException {
        QuestionnaireResult res = new QuestionnaireResult(stage, p);

        Question q = new Question();
        q.setQuestionJson("not a JSON");

        try {
            q.answer("answer", res);
            Assert.fail();
        } catch (ParseException ignored) {
        }

        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "not a type");
        q.setQuestionJson(JQuestion.toString());
        try {
            q.answer("answer", res);
            Assert.fail();
        } catch (FormatException ignored) {
        }
    }

    @Test
    public void ID() {
        Assert.assertEquals(1, open.getStageID().getStageIndex());
        Assert.assertEquals(1, multi.getStageID().getStageIndex());

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

    private Question buildOpenQuestion(QuestionnaireStage stage) {
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "open");
        JQuestion.put("question", "how much");
        return new Question(stage.getQuestions().size(), stage, JQuestion.toString());
    }

    private Question buildmultiChoiceQuestion(QuestionnaireStage stage) {

        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "multiChoice");
        JQuestion.put("question", "who?");
        JQuestion.put("possibleAnswers", List.of("me", "you", "no one", "we both"));
        return new Question(stage.getQuestions().size(), stage, JQuestion.toString());
    }
}
