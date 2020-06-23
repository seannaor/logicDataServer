package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class EnviromentController {

    private ExperimenteeService expee;

    @Autowired
    public EnviromentController(ExperimenteeService expee) {
        this.expee = expee;
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