package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import org.json.simple.JSONObject;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public JSONObject getAsJson() {
        JSONObject json = new JSONObject();
        json.put("source stage","questionnaire");
        return json;
    }
}
