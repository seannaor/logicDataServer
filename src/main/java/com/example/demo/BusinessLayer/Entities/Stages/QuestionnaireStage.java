package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import net.minidev.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage {

    @OneToMany(mappedBy = "questionnaireStage")
    private List<Question> questions = new ArrayList<>();

    public QuestionnaireStage() {
        super();
    }

    public QuestionnaireStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
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
}
