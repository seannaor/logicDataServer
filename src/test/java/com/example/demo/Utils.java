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
        stages.add(getStumpCodeMap());
        stages.add(getStumpTagMap());

        return stages;
    }

    public static Map<String,Object> getStumpInfoMap(){
        return getInfoStage("some information and stuff");
    }

    public static Map<String,Object> getStumpQuestionsMap(){
        Map<String,Object> q = Map.of("questionType", "multiChoice",
                "question", "who",
                "possibleAnswers",List.of("me","you","no one","we both"));


        return Map.of("type","questionnaire",
                "stage",Map.of("questions", List.of(getOpenQuestion("how much"),q)));
    }

    public static Map<String,Object> getStumpCodeMap(){
        return Map.of("type","code",
                "stage",
                Map.of("description", "design me a system that can create & manage & run experiments",
                        "template", "",
                        "language", "C++",
                        "requirements",List.of("do that","do this","do stuff")));
    }

    public static Map<String,Object> getStumpTagMap(){
        return Map.of("type","tag",
                "stage",
                Map.of("codeStageIndex", 3));
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
        experimenteeBusiness.fillInStage(code, Map.of("data", Map.of("code", "return -1")));
    }

    public static int fillInTagging(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        experimenteeBusiness.fillInStage(code, Map.of("data", buildParticipantTag()));
        return 3;
    }

    public static void fillInTagging(GraderBusiness graderBusiness, Participant p) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        graderBusiness.fillInStage(p, buildParticipantTag());
    }

    private static Map<String,Object> getInfoStage(String info){
        return  Map.of("type","info","stage",Map.of("text",info));
    }

    private static Map<String,Object> getQuestionnaireStage(List<Map<String,Object>> Questions){
        return  Map.of("type","questionnaire","stage",Map.of("questions",Questions));
    }

    private static Map<String,Object> getOpenQuestion(String q){
        return Map.of("questionType", "open","question",q);
    }


    public static List<Map<String,Object>> buildSimpleExp(List<String> questions) {

        List<Map<String,Object>> mapQuestions = new ArrayList<>();

        for (String question : questions) {
            Map<String,Object> q1 = getOpenQuestion(question);
            mapQuestions.add(q1);
        }

        return List.of(getInfoStage("some information and stuff"),getQuestionnaireStage(mapQuestions));
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
