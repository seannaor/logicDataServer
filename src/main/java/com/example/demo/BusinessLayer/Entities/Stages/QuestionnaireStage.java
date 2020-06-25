package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage {

    @OneToMany(mappedBy = "questionnaireStage")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Question> questions = new ArrayList<>();

    public QuestionnaireStage() {
        super();
    }


    public QuestionnaireStage(List<Map<String, Object>> JQuestions) throws ParseException {
        questions = new ArrayList<>();
        int QIndx = 0;
        for (Map<String, Object> mapQuestion : JQuestions) {
            String strQuestion = JSONObject.toJSONString(mapQuestion);
            JSONObject JQuestion = (JSONObject) (new JSONParser()).parse(strQuestion);
            questions.add(buildQuestion(JQuestion, QIndx++));
        }
    }

    @Override
    public void setExperiment(Experiment experiment) {
        super.setExperiment(experiment);
        for (Question q : this.questions) {
            q.setStageId(this.getStageID());
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
    public Map<String, Object> getAsMap() {
        List<JSONObject> questions = new LinkedList<>();
        for (Question q : this.questions) {
            questions.add(q.getQuestionJson());
        }
        return Map.of("type", getType(), "stage", (Map.of("questions", questions)));
    }

    @Override
    public String getType() {
        return "questionnaire";
    }

    // if old is null, new QuestionnaireResult will be created, else, old will be chanced
    @Override
    public QuestionnaireResult fillQuestionnaire(Map<String, Object> data, QuestionnaireResult old) throws FormatException, ParseException, NotInReachException, NotExistException {
        if (old == null) {
            old = new QuestionnaireResult();
        }

        List<String> SAnswers = validate(data);
        List<Answer> answers = new LinkedList<>();

        for (int i = 0; i < this.questions.size(); i++) {
            Question q = getQuestion(i);
            Answer ans = q.answer(SAnswers.get(i));
            answers.add(ans);
        }
        old.setAnswers(answers);
        return old; // old is actually new now :D
    }


    public Question getQuestion(int i) throws NotExistException {
        for (Question q : questions) {
            if (q.getIndex() == i) return q;
        }
        throw new NotExistException("question", i + "");
    }

    private Question buildQuestion(JSONObject jQuestion, int idx) {
        Question newQuestion = new Question(jQuestion.toString());
        newQuestion.setQuestionIndex(idx);
        newQuestion.setQuestionnaireStage(this);
        return newQuestion;
    }

    // validate answers list and return it
    private List<String> validate(Map<String, Object> data) throws FormatException {
        List<String> answers;
        try {
            answers = (List<String>) data.get("answers");
            if (answers != null && answers.size() == this.questions.size())
                return answers;
        } catch (Exception ignored) {
        }
        throw new FormatException("list of answers");
    }
}
