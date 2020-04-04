package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.json.simple.JSONObject;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage {

    @OneToMany(mappedBy = "questionnaireStage")
    private Set<Question> questions;

    public QuestionnaireStage() {
        super();
    }

    public QuestionnaireStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public QuestionnaireStage(Experiment experiment) {
        super(experiment);
        this.questions = new HashSet<>();
    }

    public QuestionnaireStage(List<JSONObject> JQuestions, Experiment experiment) {
        super(experiment);
        questions = new HashSet<>();
        int QIdx = 1;
        for (JSONObject JQuestion : JQuestions) {
            Question q = new Question(QIdx, this, JQuestion.toString());
            questions.add(q);
            QIdx++;
        }
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public JSONObject getJson() {
        JSONObject jStage = new org.json.simple.JSONObject();
        jStage.put("type","questionnaire");
        List<String> jQuestions = new LinkedList<>();
        for (Question q : questions) {
            jQuestions.add(q.getQuestionJson());
        }
        jStage.put("questions", jQuestions);
        return jStage;
    }
}
