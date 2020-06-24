package com.example.demo;


import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.GraderBusiness;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Utils {

    public static List<Map<String,Object>> buildStages() {
        List<Map<String,Object>> stages = new ArrayList<>();

        stages.add(getStumpInfoMap());
        stages.add(getStumpQuestionsMap());
//        stages.add(getStumpCodeStage());
//        stages.add(getStumpTaggingStage());

        return stages;
    }

    public static Map<String,Object> getStumpInfoMap(){
        return Map.of("type","info","info", "some information and stuff");
    }
    public static Map<String,Object> getStumpQuestionsMap(){
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("type", "questionnaire");
        List<JSONObject> questions = new ArrayList<>();
        Map<String,Object> q1 = Map.of("questionType", "open","question", "how much");


//        JSONObject q2 = new JSONObject();
//        q2.put("questionType", "multiChoice");
//        q2.put("question", "who?");
//        List<String> answers = new ArrayList<>();
//        answers.add("me");
//        answers.add("you");
//        answers.add("no one");
//        answers.add("we both");
//        q2.put("possibleAnswers", answers);
//        questions.add(q2);
//        questionnaire.put("questions", questions);

        return Map.of("type","questionnaire","questions", List.of(q1));
    }
//    public static Map<String,Object> getStumpInfoMap(){
//        return Map.of("type","info","info", "some information and stuff");
//    }
//    public static Map<String,Object> getStumpInfoMap(){
//        return Map.of("type","info","info", "some information and stuff");
//    }

    public static JSONObject buildResearcher() {
        JSONObject researcher = new JSONObject();
        researcher.put("email", "manager@post.ac.il");
        researcher.put("username", "manager");
        researcher.put("password", "password");
        return researcher;
    }

    public static JSONObject getStumpInfoStage() {
        JSONObject info = new JSONObject();
        info.put("type", "info");
        info.put("info", "some information and stuff");
        return info;
    }

    public static JSONObject getStumpCodeStage() {
        JSONObject code = new JSONObject();
        code.put("type", "code");
        code.put("description", "design me a system that can create & manage & run experiments");
        code.put("template", "");
        code.put("language", "C++");
        List<String> requirements = new ArrayList<>();
        requirements.add("do that");
        requirements.add("do this");
        requirements.add("do stuff");
        code.put("requirements", requirements);
        return code;
    }

    public static JSONObject getStumpQuestionsStage() {
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("type", "questionnaire");
        List<JSONObject> questions = new ArrayList<>();
        JSONObject q1 = new JSONObject();

        q1.put("questionType", "open");
        q1.put("question", "how much");
        questions.add(q1);

        JSONObject q2 = new JSONObject();
        q2.put("questionType", "multiChoice");
        q2.put("question", "who?");
        List<String> answers = new ArrayList<>();
        answers.add("me");
        answers.add("you");
        answers.add("no one");
        answers.add("we both");
        q2.put("possibleAnswers", answers);
        questions.add(q2);
        questionnaire.put("questions", questions);
        return questionnaire;
    }

    public static JSONObject getStumpTaggingStage() {
        JSONObject JTagging = new JSONObject();
        JTagging.put("type", "tagging");
        JTagging.put("codeIndex", 2);
        return JTagging;
    }

    public static Experiment buildExp(CreatorBusiness creatorBusiness, ManagementUser manager) throws NotExistException, FormatException, ExistException {
        List<Map<String,Object>> stages = buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        return manager.getExperimentByName("The Experiment");
    }

    public static void fillInExp(ExperimenteeBusiness experimenteeBusiness, UUID code, boolean finish) throws NotExistException, CodeException, ExpEndException, ParseException, FormatException, NotInReachException {
        // pass info (first) stage
        experimenteeBusiness.getNextStage(code);

        // fill in questions (second) stage, good format should pass
        fillInQuestionnaire(experimenteeBusiness, code);
        experimenteeBusiness.getNextStage(code);

        // fill in code (third) stage, good format should pass
        fillInCode(experimenteeBusiness, code);
        experimenteeBusiness.getNextStage(code);

        fillInTagging(experimenteeBusiness, code);

        if (finish)
            try {
                experimenteeBusiness.getNextStage(code);
            } catch (ExpEndException ignore) {
            }
    }

    public static int fillInQuestionnaire(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        experimenteeBusiness.fillInStage(code,
                Map.of("data", Map.of("answers", List.of("a lot!", "22"))));
        return 2;
    }

    public static void fillInCode(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
//        JSONObject ans = new JSONObject();
//        ans.put("stageType", "code");
//        ans.put("userCode", "return -1");
        experimenteeBusiness.fillInStage(code, Map.of("data", Map.of("code", "return -1")));
    }

    public static int fillInTagging(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        experimenteeBusiness.fillInStage(code, Map.of("data", buildParticipantTag()));
        return 3;
    }

    public static void fillInQuestionnaire(GraderBusiness graderBusiness, Participant p) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "questionnaire");
        JSONObject ans1 = new JSONObject();
        ans1.put("answer", "a lot!");
        ans.put("1", ans1);
        JSONObject ans2 = new JSONObject();
        ans2.put("answer", 3);
        ans.put("2", ans2);
        graderBusiness.fillInStage(p, ans);
    }

    public static void fillInCode(GraderBusiness graderBusiness, Participant p) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "code");
        ans.put("userCode", "return -1");
        graderBusiness.fillInStage(p, ans);
    }

    public static void fillInTagging(GraderBusiness graderBusiness, Participant p) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        graderBusiness.fillInStage(p, buildParticipantTag());
    }

    public static List<Map<String,Object>> buildSimpleExp(List<String> questions) {
        List<JSONObject> stages = new ArrayList<>();

        JSONObject info = new JSONObject();
        info.put("type", "info");
        info.put("info", "some information and stuff");

        stages.add(info);

        JSONObject questionnaire = new JSONObject();
        questionnaire.put("type", "questionnaire");
        List<JSONObject> JQuestions = new ArrayList<>();

        for (String question : questions) {
            JSONObject q1 = new JSONObject();

            q1.put("questionType", "open");
            q1.put("question", question);
            JQuestions.add(q1);

        }
        questionnaire.put("questions", JQuestions);
        stages.add(questionnaire);
        return List.of();
    }

    public static GradingTask buildSimpleGradingTask(CreatorBusiness creatorBusiness, DataCache cache, ManagementUser manager, Experiment exp) throws NotExistException, FormatException {
        int tid = creatorBusiness.addGradingTask(manager.getBguUsername(), exp.getExperimentId(), "TestGradingTask",
                buildSimpleExp(List.of("what do you think about the experimentee results?", "state your favorite food")), List.of(1, 2)
                , buildSimpleExp(List.of("what is your best score in minesweeper?")));
        return cache.getGradingTaskById(manager.getBguUsername(), exp.getExperimentId(), tid);
    }

    public static Map<String, Object> getPersonalAnswers() {
        return Map.of("data", Map.of("answers", List.of("100")));
    }

    public static Map<String, Object> getGradingAnswers(List<String> answers) {
        return Map.of("data", Map.of("answers", answers));
    }

    public static Map<String, Object> buildParticipantTag() {
        Map<String,Object> fromTo = Map.of("from",Map.of("row",1,"col",0),
                "to",Map.of("row",1,"col",7));
        return Map.of("tags", List.of(List.of(fromTo), List.of(fromTo), List.of(fromTo)));
    }
}
