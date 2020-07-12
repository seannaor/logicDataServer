package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class EnviromentController {

    private ExperimenteeService expee;
    private final RestTemplate restTemplate;
    private final String tokenUrl = "https://judge0.p.rapidapi.com/";
    private final String judge0Key = "460509d2cemsh25d3565f9399f0bp1004c7jsne3f4ac235341";
//    header should be : Map.of( "X-RapidAPI-Host", "judge0.p.rapidapi.com","X-RapidAPI-Key", judge0Key)


    @Autowired
    public EnviromentController(ExperimenteeService expee, RestTemplateBuilder restTemplateBuilder) {
        this.expee = expee;
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostMapping("/login")
    public boolean logIn(@RequestBody String accessCode) {
        System.out.println("/login " + accessCode);
        return expee.beginParticipation(accessCode);
    }

    @GetMapping("/getStages/{accessCode}")
    public Map<String, Object> getStages(@PathVariable String accessCode) {
        System.out.println("/getStages " + accessCode);
        return expee.reachableStages(accessCode);
    }

    @PostMapping("/submitStage/{accessCode}")
    public Map<String, Object> submitStage(@PathVariable String accessCode,
                                           @RequestBody Map<String, Object> stageInfo) {
        System.out.println("/submitStage " + accessCode + " data " + stageInfo);
        return expee.fillInStage(accessCode, stageInfo);
    }

    @PostMapping("/runCode")
    public Object runCode(@RequestBody String code, @RequestBody String language) {
        return expee.runCode(tokenUrl,judge0Key,code,language);
    }

    @GetMapping("/getLanguages")
    public Map<String, Object> getLanguages() {
        return expee.getLanguages(tokenUrl,judge0Key);
    }


//    @RequestMapping("/next_stage")
//    public Map<String, Object> nextStage(@RequestParam String code) {
//        System.out.println("experimentee/next_stage " + code);
//        return expee.getNextStage(code);
//    }
//
//    @RequestMapping("/current_stage")
//    public Map<String, Object> currStage(@RequestParam String code) {
//        System.out.println("experimentee/current_stage " + code);
//        return expee.getCurrentStage(code);
//    }
//
//    @RequestMapping("/get_stage/{id}")
//    public  Map<String, Object> getStageAt(
//            @PathVariable("id") int id, @RequestParam String code) {
//        System.out.println("experimentee/get_stage "+id+" " + code);
//        return expee.getStageAt(code,id);
//    }
}