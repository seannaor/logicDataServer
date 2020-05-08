package com.example.demo.BusinessLayer.Entities.Results;

import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class AnswersWrapper implements ResultWrapper {

    private List<Answer> answers = new LinkedList<>();

    public void addAns(Answer ans){
        if(!answers.contains(ans))
            answers.add(ans);
    }
    @Override
    public JSONObject getAsJson() {
        return null;
    }
}