package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
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

    public QuestionnaireStage(List<JSONObject> JQuestions, Experiment experiment) {
        super(experiment);
        questions = new ArrayList<>();
        int QIdx = 1;
        for (JSONObject JQuestion : JQuestions) {
            Question q = new Question(QIdx, this, JQuestion.toString());
            questions.add(q);
            QIdx++;
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

    public JSONObject getJson() {
        JSONObject jStage = new org.json.simple.JSONObject();
        jStage.put("type", "questionnaire");
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
    public List<Answer> fillQuestionnaire(JSONObject data, Participant participant) throws FormatException, ParseException {
        List<Answer> answers = new ArrayList<>();

        for (Question q : questions) {
            int i = q.getIndex();
            if (!data.containsKey(i))
                throw new FormatException("answer #" + i);

            answers.add(q.answer(data.get(i),participant));
        }
        return answers;
    }
}
