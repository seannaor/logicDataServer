package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import net.minidev.json.JSONObject;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage {

    @OneToMany(mappedBy = "questionnaireStage")
    //@NotFound(action= NotFoundAction.IGNORE)
    private Set<Question> questions;

    public QuestionnaireStage() {
        super();
    }

    public QuestionnaireStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public QuestionnaireStage(Experiment experiment) {
        super(experiment);
        questions = new HashSet<>();
    }

    public QuestionnaireStage(List<JSONObject> JQuestions, Experiment experiment) {
        super(experiment);
        questions = new HashSet<>();
        int QIdx = 1;
        for (JSONObject JQuestion : JQuestions) {
            Question q = new Question(QIdx, this, JQuestion);
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
}
