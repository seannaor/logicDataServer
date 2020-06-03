package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
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

public class QuestionnaireUnitTests {

    private QuestionnaireStage questionnaireStage;
    private List<Question> questions;
    private Participant participant;
    private Experiment exp;

    @BeforeEach
    private void init() {
        exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        participant = new Participant(exp);
        participant.setParticipantId(10);

        questionnaireStage = new QuestionnaireStage(exp);
        questions = List.of(buildOpenQuestion(), buildmultiChoiceQuestion());
        questionnaireStage.addQuestion(questions.get(0));
        questionnaireStage.addQuestion(questions.get(1));

    }

    @Test
    public void fillIn() throws NotInReachException, NotExistException, ParseException, FormatException {
        List<String> SAnswers  = List.of("a lot", "you");
        QuestionnaireResult res = questionnaireStage.fillQuestionnaire(SAnswers, participant);

        List<Answer> answers = res.getAnswers();

        Assert.assertEquals(SAnswers.get(0),answers.get(0).getAnswer());
        Assert.assertEquals(SAnswers.get(1),answers.get(1).getAnswer());
    }

    @Test
    public void buildQuestionnaire() throws FormatException {

        List<JSONObject> JQuestions = (List<JSONObject>) getStumpQuestionsStage().get("questions");
        QuestionnaireStage questionnaire = new QuestionnaireStage(JQuestions,exp);

        Assert.assertEquals(JQuestions.size(),questionnaire.getQuestions().size());
        Assert.assertEquals(JQuestions.get(0),questionnaire.getQuestions().get(0).getQuestionJson());
        Assert.assertEquals(JQuestions.get(1),questionnaire.getQuestions().get(1).getQuestionJson());
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
    public void buildFromJson() throws FormatException {
        JSONObject JQuestionnaireStage = getStumpQuestionsStage();
        Stage stage = Stage.parseStage(JQuestionnaireStage,exp);
        Assert.assertEquals("questionnaire",stage.getType());
    }

    private Question buildOpenQuestion() {
        Question q = new Question(1,questionnaireStage,"");
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "open");
        JQuestion.put("question", "how much");
        q.setQuestionJson(JQuestion.toString());
        return q;
    }

    private Question buildmultiChoiceQuestion() {
        Question q= new Question(2,questionnaireStage,"");
        JSONObject JQuestion = new JSONObject();
        JQuestion.put("questionType", "multiChoice");
        JQuestion.put("question", "who?");
        JQuestion.put("possibleAnswers", List.of("me","you","no one","we both"));
        q.setQuestionJson(JQuestion.toString());
        return q;
    }

}
