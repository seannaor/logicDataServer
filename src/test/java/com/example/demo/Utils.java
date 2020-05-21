package com.example.demo;


import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

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

        q1.put("type", "open");
        q1.put("question", "WTF?!?");
        questions.add(q1);

        JSONObject q2 = new JSONObject();
        q2.put("type", "american");
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

    public static void fillInExp(ExperimenteeBusiness experimenteeBusiness, Experimentee expee) throws CodeException, ExpEndException, ParseException, FormatException, NotInReachException {
        // pass info (first) stage
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        // fill in questions (second) stage, good format should pass
        fillInQuestionnaire(experimenteeBusiness, expee);
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        // fill in code (third) stage, good format should pass
        fillInCode(experimenteeBusiness, expee);
//        experimenteeBusiness.getNextStage(expee.getAccessCode());
//
//        fillInTagging(experimenteeBusiness, expee);
    }

    public static void fillInQuestionnaire(ExperimenteeBusiness experimenteeBusiness, Experimentee expee) throws NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "questionnaire");
        JSONObject ans1 = new JSONObject();
        ans1.put("answer", "WTF!!!");
        ans.put("1", ans1);
        JSONObject ans2 = new JSONObject();
        ans2.put("answer", 3);
        ans.put("2", ans2);
        experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
    }

    public static void fillInCode(ExperimenteeBusiness experimenteeBusiness, Experimentee expee) throws NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "code");
        ans.put("userCode", "return -1");
        experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
    }

    public static void fillInTagging(ExperimenteeBusiness experimenteeBusiness, Experimentee expee) throws NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
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
        experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
    }

    public static GradingTask buildGradingTask(DataCache cache, Grader grader, Experiment base, Experiment general, Experiment grading) throws ExistException, NotExistException, FormatException {
        GradingTask task = new GradingTask("test", base, general, grading);
        task.setStagesByIdx(List.of(1, 2));
        cache.addGradingTask(task);
        cache.addGraderToGradingTask(task, grader);
        return task;
    }
}
