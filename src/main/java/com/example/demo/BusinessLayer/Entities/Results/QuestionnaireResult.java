package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import org.json.simple.JSONObject;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "questionnaire_results")
public class QuestionnaireResult extends Result {
    @OneToMany(mappedBy = "questionnaireResult")
    private List<Answer> answers;

    public QuestionnaireResult() { }

    public QuestionnaireResult(QuestionnaireStage questionnaireStage, Participant participant) {
        super(questionnaireStage, participant);
        this.answers = new ArrayList<>();
    }

    public void addAns(Answer ans){
        if(!answers.contains(ans))
            answers.add(ans);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public Map<String, Object> getAsMap() {
        List<String> answers = new ArrayList<>();
        for(Answer ans : this.answers){
            answers.add(ans.getAnswerJson());
        }
        return Map.of("answers",answers);
    }
}
