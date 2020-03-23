package com.example.demo;

import net.minidev.json.JSONObject;

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
        researcher.appendField("email","shit@fuck.ac.il");
        researcher.appendField("username","manager");
        researcher.appendField("password","password");
        return researcher;
    }

    private static JSONObject getStumpInfoStage(){
        JSONObject info = new JSONObject();
        info.appendField("type", "info");
        info.appendField("info", "some information and stuff");
        return info;
    }

    private static JSONObject getStumpCodeStage(){
        JSONObject code = new JSONObject();
        code.appendField("type","code");
        code.appendField("description","design me a system that can create & manage & run experiments");
        code.appendField("template","");
        List<String> requirements = new ArrayList<>();
        requirements.add("do that");
        requirements.add("do this");
        requirements.add("fuck off");
        code.appendField("requirements",requirements);
        return code;
    }

    private static JSONObject getStumpQuestionsStage(){
        JSONObject questionnaire = new JSONObject();
        questionnaire.appendField("type", "questionnaire");
        List<JSONObject> questions = new ArrayList<>();
        JSONObject q1 = new JSONObject();

        q1.appendField("type", "open");
        q1.appendField("question", "WTF?!?");
        questions.add(q1);

        JSONObject q2 = new JSONObject();
        q2.appendField("type", "multi");
        q2.appendField("question", "who?");
        List<String> answers = new ArrayList<>();
        answers.add("me");
        answers.add("you");
        answers.add("no one");
        answers.add("we both");
        q2.appendField("answers", answers);
        questions.add(q2);
        questionnaire.appendField("questionnaire", questions);
        return questionnaire;
    }
}
