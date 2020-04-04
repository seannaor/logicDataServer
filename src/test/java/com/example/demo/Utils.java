package com.example.demo;


import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<JSONObject> buildStages() {
        List<JSONObject> stages = new ArrayList<>();

        stages.add(getStumpInfoStage());
        stages.add(getStumpQuestionsStage());
        stages.add(getStumpCodeStage());

        return stages;
    }

    public static JSONObject buildResearcher(){
        JSONObject researcher = new JSONObject();
        researcher.put("email","shit@fuck.ac.il");
        researcher.put("username","manager");
        researcher.put("password","password");
        return researcher;
    }

    private static JSONObject getStumpInfoStage(){
        JSONObject info = new JSONObject();
        info.put("type", "info");
        info.put("info", "some information and stuff");
        return info;
    }

    private static JSONObject getStumpCodeStage(){
        JSONObject code = new JSONObject();
        code.put("type","code");
        code.put("description","design me a system that can create & manage & run experiments");
        code.put("template","");
        List<String> requirements = new ArrayList<>();
        requirements.add("do that");
        requirements.add("do this");
        requirements.add("fuck off");
        code.put("requirements",requirements);
        return code;
    }

    private static JSONObject getStumpQuestionsStage(){
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("type", "questionnaire");
        List<JSONObject> questions = new ArrayList<>();
        JSONObject q1 = new JSONObject();

        q1.put("type", "open");
        q1.put("question", "WTF?!?");
        questions.add(q1);

        JSONObject q2 = new JSONObject();
        q2.put("type", "multi");
        q2.put("question", "who?");
        List<String> answers = new ArrayList<>();
        answers.add("me");
        answers.add("you");
        answers.add("no one");
        answers.add("we both");
        q2.put("answers", answers);
        questions.add(q2);
        questionnaire.put("questions", questions);
        return questionnaire;
    }
}
