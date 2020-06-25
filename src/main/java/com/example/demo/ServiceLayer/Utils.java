package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import java.util.List;
import java.util.Map;

public class Utils {
    public static Map<String, Object> makeInfoStage(String info) {
        return Map.of(
                "text", info
        );
    }

    public static Map<String, Object> makeOpenQuestion(String question) {
        return Map.of(
                "question", question,
                "questionType", "open"
        );
    }

    public static Map<String, Object> makeMultiChoiceQuestion(String question, List<String> possibleAnswers) {
        return Map.of(
                "question", question,
                "questionType", "multiChoice",
                "possibleAnswers", possibleAnswers
        );
    }

    public static Map<String, Object> makeQuestionnaireStage(List<Map<String, Object>> qs) {
        return Map.of(
                "questions", qs
        );
    }

    public static Map<String, Object> makeCodeStage(String template, String language) {
        return Map.of(
                "template", template,
                "language", language
        );
    }

    public static Map<String, Object> makeQuestionnaireResult(List<String> answers) {
        return Map.of(
                "answers", answers
        );
    }

    public static Map<String, Object> makeCodeResult(String code) {
        return Map.of(
                "code", code
        );
    }

    public static Map<String, Object> makeStageAndResult(Stage stage, Result result) {
        return result == null ? stage.getAsMap() :
                Map.of(
                "type", stage.getType(),
                "stage", stage.getAsMap().get("stage"),
                "result", result.getAsMap()
        );
    }

    public static Map<String, Object> makeGetStagesResponse(String expName, List<Map<String, Object>> stages) {
        return Map.of(
                "expName", expName,
                "stages", stages
        );
    }

    public static Map<String, Object> makeSubmitResponseInfo(String info) {
        return Map.of(
                "type", "info",
                "stage", makeInfoStage(info)
        );
    }

    public static Map<String, Object> makeSubmitResponseQuestionnaire(List<Map<String, Object>> qs) {
        return Map.of(
                "type", "questionnaire",
                "stage", makeQuestionnaireStage(qs)
        );
    }

    public static Map<String, Object> makeSubmitResponseCode(String template, String language) {
        return Map.of(
                "type", "code",
                "stage", makeCodeStage(template, language)
        );
    }
}
