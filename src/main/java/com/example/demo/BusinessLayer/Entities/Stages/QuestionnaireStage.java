package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage {

    @OneToMany(mappedBy = "questionnaireStage")
    private List<Question> questions = new ArrayList<>();

    public QuestionnaireStage() {
        super();
    }

    public QuestionnaireStage(Experiment experiment) {
        super(experiment);
    }

    public QuestionnaireStage(List<JSONObject> JQuestions, Experiment experiment) throws FormatException {
        super(experiment);
        questions = new ArrayList<>();
        int QIdx = 1;
        for (JSONObject JQuestion : JQuestions) {
            questions.add(buildQuestion(JQuestion,QIdx++));
        }
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    @Override
    public JSONObject getJson() {
        JSONObject jStage = new org.json.simple.JSONObject();
        List<String> jQuestions = new LinkedList<>();
        for (Question q : questions) {
            jQuestions.add(q.getQuestionJson());
        }
        jStage.put("questions", jQuestions);
        return jStage;
    }

    @Override
    public String getType() {
        return "questionnaire";
    }

    @Override
    public QuestionnaireResult fillQuestionnaire(JSONObject data, Participant participant) throws FormatException, ParseException, NotInReachException {
        QuestionnaireResult questionnaireResult = (QuestionnaireResult)participant.getResult(this.getStageID().getStageIndex());
        if(questionnaireResult == null) {
            questionnaireResult = new QuestionnaireResult(this, participant);
        }
        for (Question q : questions) {
            int i = q.getIndex();
            if (!data.containsKey(i+""))
                throw new FormatException("answer #" + i);

            q.answer(data.get(i+""), questionnaireResult); //adds the new answer to the questionnaireResult automatically
        }
        return questionnaireResult;
    }

    private Question buildQuestion(JSONObject jQuestion, int QIdx) throws FormatException{
        return new Question(QIdx, this, jQuestion.toString());
    }
}
