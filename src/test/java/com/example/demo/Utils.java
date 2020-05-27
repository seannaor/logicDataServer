package com.example.demo;


import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.GraderBusiness;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

    public static List<JSONObject> buildStages() {
        List<JSONObject> stages = new ArrayList<>();

        stages.add(getStumpInfoStage());
        stages.add(getStumpQuestionsStage());
        stages.add(getStumpCodeStage());
        stages.add(getStumpTaggingStage());

        return stages;
    }

    public static JSONObject buildResearcher() {
        JSONObject researcher = new JSONObject();
        researcher.put("email", "shit@fuck.ac.il");
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
        List<String> requirements = new ArrayList<>();
        requirements.add("do that");
        requirements.add("do this");
        requirements.add("fuck off");
        code.put("requirements", requirements);
        return code;
    }

    public static JSONObject getStumpQuestionsStage() {
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("type", "questionnaire");
        List<JSONObject> questions = new ArrayList<>();
        JSONObject q1 = new JSONObject();

        q1.put("questionType", "open");
        q1.put("question", "WTF?!?");
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
        List<JSONObject> stages = buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        return manager.getExperimentByName("The Experiment");
    }

    public static void fillInExp(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, CodeException, ExpEndException, ParseException, FormatException, NotInReachException {
        // pass info (first) stage
        experimenteeBusiness.getNextStage(code);

        // fill in questions (second) stage, good format should pass
        fillInQuestionnaire(experimenteeBusiness, code);
        experimenteeBusiness.getNextStage(code);

        // fill in code (third) stage, good format should pass
        fillInCode(experimenteeBusiness, code);
        experimenteeBusiness.getNextStage(code);

        fillInTagging(experimenteeBusiness, code);

        try {
            experimenteeBusiness.getNextStage(code);
        } catch (ExpEndException ignore) {
        }
    }

    public static int fillInQuestionnaire(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "questionnaire");
        JSONObject ans1 = new JSONObject();
        ans1.put("answer", "WTF!!!");
        ans.put("1", ans1);
        JSONObject ans2 = new JSONObject();
        ans2.put("answer", 3);
        ans.put("2", ans2);
        experimenteeBusiness.fillInStage(code, ans);
        return 2;
    }

    public static void fillInCode(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "code");
        ans.put("userCode", "return -1");
        experimenteeBusiness.fillInStage(code, ans);
    }

    public static int fillInTagging(ExperimenteeBusiness experimenteeBusiness, UUID code) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "tagging");

        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);
        ans.put(0, tag1);

        JSONObject tag2 = new JSONObject();
        tag2.put("start_loc", 0);
        tag2.put("length", 10);
        ans.put(1, tag2);

        JSONObject tag3 = new JSONObject();
        tag3.put("start_loc", 0);
        tag3.put("length", 10);
        ans.put(2, tag3);
        experimenteeBusiness.fillInStage(code, ans);
        return 3;
    }

    public static void fillInQuestionnaire(GraderBusiness graderBusiness, Participant p) throws NotExistException, NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "questionnaire");
        JSONObject ans1 = new JSONObject();
        ans1.put("answer", "WTF!!!");
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
        JSONObject ans = new JSONObject();
        ans.put("stageType", "tagging");

        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);
        ans.put(0, tag1);

        JSONObject tag2 = new JSONObject();
        tag2.put("start_loc", 0);
        tag2.put("length", 10);
        ans.put(1, tag2);

        JSONObject tag3 = new JSONObject();
        tag3.put("start_loc", 0);
        tag3.put("length", 10);
        ans.put(2, tag3);
        graderBusiness.fillInStage(p, ans);
    }

    private static List<JSONObject> buildSimpleExp(List<String> questions) {
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
        return stages;
    }

    public static GradingTask buildSimpleGradingTask(CreatorBusiness creatorBusiness, DataCache cache, ManagementUser manager, Experiment exp) throws NotExistException, FormatException {
        int tid = creatorBusiness.addGradingTask(manager.getBguUsername(), exp.getExperimentId(), "TestGradingTask",
                buildSimpleExp(List.of("what do you think about the experimentee results?", "state your favorite curs word")), List.of(1, 2)
                , buildSimpleExp(List.of("what is your best score in minesweeper?")));
        return cache.getGradingTaskById(manager.getBguUsername(), exp.getExperimentId(), tid);
    }

    public static JSONObject getPersonalAnswers() {
        JSONObject JAnswers = new JSONObject();
        JAnswers.put("stageType", "questionnaire");
        JSONObject ans1 = new JSONObject();
        ans1.put("answer", "I saw an arab dude fucking a sheep.. ho minesweeper? I don't know");
        JAnswers.put("1", ans1);
        return JAnswers;
    }

    public static JSONObject getGradingAnswers(List<String> answers) {
        JSONObject JAnswers = new JSONObject();
        JAnswers.put("stageType", "questionnaire");
        for (int i = 0; i < answers.size(); i++) {
            JSONObject ans1 = new JSONObject();
            ans1.put("answer", answers.get(i));
            JAnswers.put(i + 1 + "", ans1);
        }
        return JAnswers;
    }
}
