package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    public Map<String, Object> getAsMap() {
        List<JSONObject> questions = new LinkedList<>();
        for (Question q : this.questions) {
            questions.add(q.getQuestionJson());
        }
        return Map.of("questions", questions);
    }

    @Override
    public String getType() {
        return "questionnaire";
    }

    @Override
    public QuestionnaireResult fillQuestionnaire(List<String> answers, Participant participant) throws FormatException, ParseException, NotInReachException, NotExistException {
        QuestionnaireResult questionnaireResult = (QuestionnaireResult)participant.getResult(this.getStageID().getStageIndex());
        if(questionnaireResult == null) {
            questionnaireResult = new QuestionnaireResult(this, participant);
        }

        for (int i = 0; i < questions.size(); i++) {
            Question q = getQuestion(i);
            q.answer(answers.get(i),questionnaireResult);
        }
//        for (Question q : questions) {
//            int i = q.getIndex();
//            if (!data.containsKey(i+""))
//                throw new FormatException("answer #" + i);
//
//            q.answer(data.get(i+""), questionnaireResult); //adds the new answer to the questionnaireResult automatically
//        }
        return questionnaireResult;
    }

    private Question getQuestion(int i) throws NotExistException {
        for(Question q: questions){
            if(q.getIndex()==i+1) return q;
        }
        throw new NotExistException("question", (i+1)+"");
    }

    private Question buildQuestion(JSONObject jQuestion, int QIdx) throws FormatException{
        return new Question(QIdx, this, jQuestion.toString());
    }
}
