package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Stages.Question;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.example.demo.Utils.getStumpQuestionsStage;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuestionnaireUnitTests {

    private QuestionnaireStage questionnaireStage;
    private List<Question> questions;

    @BeforeEach
    private void init() {
        questionnaireStage = new QuestionnaireStage();
        questions = List.of(buildOpenQuestion(), buildMultiChoiceQuestion());
        questions.get(0).setQuestionIndex(0);
        questions.get(1).setQuestionIndex(1);
        questionnaireStage.addQuestion(questions.get(0));
        questionnaireStage.addQuestion(questions.get(1));
    }

    @Test
    public void fillIn() throws NotInReachException, NotExistException, ParseException, FormatException {
        List<String> SAnswers = List.of("a lot", "you");
        QuestionnaireResult res = questionnaireStage.fillQuestionnaire(Map.of("answers", SAnswers), null);

        List<Answer> answers = res.getAnswers();

        Assert.assertEquals(SAnswers.get(0), answers.get(0).getAnswer());
        Assert.assertEquals(SAnswers.get(1), answers.get(1).getAnswer());

        // can change answers
        SAnswers = List.of("none", "me");
        res = questionnaireStage.fillQuestionnaire(Map.of("answers", SAnswers), null);

        answers = res.getAnswers();

        Assert.assertEquals(SAnswers.get(0), answers.get(0).getAnswer());
        Assert.assertEquals(SAnswers.get(1), answers.get(1).getAnswer());
    }

    @Test
    public void fillInNoAnsFail() {
        assertThrows(FormatException.class,()->{
            //no answers
            questionnaireStage.fillQuestionnaire(Map.of(), null);
        });
    }

    @Test
    public void fillInNoAnsListFail()  {
        assertThrows(FormatException.class,()->{
            //answers is not a list of strings
            questionnaireStage.fillQuestionnaire(Map.of("answers", -1), null);
        });
    }

    @Test
    public void fillInNotAllAnsFail() {
        assertThrows(FormatException.class,()->{
            //missing answers
            questionnaireStage.fillQuestionnaire(Map.of("answers", List.of()), null);
        });
    }

    @Test
    public void buildQuestionnaire() {

        List<JSONObject> JQuestions = (List<JSONObject>) getStumpQuestionsStage().get("questions");
        QuestionnaireStage questionnaire = new QuestionnaireStage(JQuestions);

        Assert.assertEquals(JQuestions.size(), questionnaire.getQuestions().size());
        Assert.assertEquals(JQuestions.get(0), questionnaire.getQuestions().get(0).getQuestionJson());
        Assert.assertEquals(JQuestions.get(1), questionnaire.getQuestions().get(1).getQuestionJson());
    }

    @Test
    public void getMap() {
        Map<String, Object> qestionnaireMap = questionnaireStage.getAsMap();
        Assert.assertTrue(qestionnaireMap.containsKey("questions"));

        Assert.assertEquals(questionnaireStage.getQuestions().size(), ((List) qestionnaireMap.get("questions")).size());

        List<JSONObject> qs = (List<JSONObject>) qestionnaireMap.get("questions");
        Assert.assertEquals(questionnaireStage.getQuestions().get(0).getQuestionJson(), qs.get(0));
        Assert.assertEquals(questionnaireStage.getQuestions().get(1).getQuestionJson(), qs.get(1));
    }

    @Test
    public void setQuestions() {
        questionnaireStage.setQuestions(List.of(questions.get(1), questions.get(0)));

        Assert.assertEquals(questions.get(0), questionnaireStage.getQuestions().get(1));
        Assert.assertEquals(questions.get(1), questionnaireStage.getQuestions().get(0));
    }

    @Test
    public void addQuestion() throws NotExistException {
        int numQuestions = questionnaireStage.getQuestions().size();
        Question newQ = questions.get(0);
        newQ.setQuestionIndex(2);
        questionnaireStage.addQuestion(newQ);
        Assert.assertEquals(numQuestions + 1, questionnaireStage.getQuestions().size());

        Assert.assertEquals(newQ, questionnaireStage.getQuestion(2));
    }

    @Test
    public void questionIndexFail() {
        assertThrows(NotExistException.class,()->{
            questionnaireStage.getQuestion(-1);
        });
    }

    @Test
    public void buildFromJson() throws FormatException {
        JSONObject JQuestionnaireStage = getStumpQuestionsStage();
        Stage stage = Stage.parseStage(JQuestionnaireStage, null);
        Assert.assertEquals("questionnaire", stage.getType());
    }

    private Question buildOpenQuestion() {
        Question q = new Question("");
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "open");
        JQuestion.put("question", "how much");
        q.setQuestionJson(JQuestion.toString());
        return q;
    }

    private Question buildMultiChoiceQuestion() {
        Question q = new Question("");
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "multiChoice");
        JQuestion.put("question", "who?");
        JQuestion.put("possibleAnswers", List.of("me", "you", "no one", "we both"));
        q.setQuestionJson(JQuestion.toString());
        return q;
    }

}
