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
import org.junit.jupiter.api.Test;

import java.util.List;

public class QuestionUnitTests {
    private Question open = buildOpenQuestion();
    private Question multi= buildmultiChoiceQuestion() ;


    @Test
    public void answer() throws ParseException, FormatException {
        Participant p = new Participant();
        p.setParticipantId(10);
        QuestionnaireStage questionnaire = new QuestionnaireStage();
        questionnaire.setStageID(new Stage.StageID(1));
        QuestionnaireResult res = new QuestionnaireResult(questionnaire,p);

        Answer openAns = open.answer("a lot",res);
        Answer multiAns = multi.answer("me",res);
        Assert.assertEquals("a lot",openAns.getAnswer());
        Assert.assertEquals("me",multiAns.getAnswer());
    }

    @Test
    public void getJson(){
        JSONObject JOpen = open.getQuestionJson();
        JSONObject JMulti = multi.getQuestionJson();

        Assert.assertTrue(JOpen.containsKey("questionType"));
        Assert.assertTrue(JOpen.containsKey("question"));
        Assert.assertTrue(JMulti.containsKey("questionType"));
        Assert.assertTrue(JMulti.containsKey("question"));
        Assert.assertTrue(JMulti.containsKey("possibleAnswers"));

        Assert.assertEquals("open",JOpen.get("questionType"));
        Assert.assertEquals("how much",JOpen.get("question"));

        Assert.assertEquals("multiChoice",JMulti.get("questionType"));
        Assert.assertEquals("who?",JMulti.get("question"));
        Assert.assertEquals(4,((List)JMulti.get("possibleAnswers")).size());
    }

    private Question buildOpenQuestion() {
        Question q = new Question();
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "open");
        JQuestion.put("question", "how much");
        q.setQuestionJson(JQuestion.toString());
        return q;
    }

    private Question buildmultiChoiceQuestion() {
        Question q= new Question();
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "multiChoice");
        JQuestion.put("question", "who?");
        JQuestion.put("possibleAnswers", List.of("me","you","no one","we both"));
        q.setQuestionJson(JQuestion.toString());
        return q;
    }
}
