package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.decode;
import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ExperimenteeRouter {

    private ExperimenteeService expee;

    @Autowired
    public ExperimenteeRouter(ExperimenteeService expee) {
        this.expee = expee;
    }

    @PostMapping("/login")
    public Map<String, Object> expeeLogin(@RequestBody String code) {
        System.out.println("/login " + code);
        return expee.beginParticipation(code);
    }

    @PostMapping("/submitStage/{accessCode}")
    public Map<String, Object> fillStage(@PathVariable String accessCode, @RequestBody Map<String, Object> stageInfo) {
        System.out.println("/fill_stage " + accessCode);
        return expee.fillInStage(accessCode, stageInfo);
    }

    @GetMapping("/getStages/{accessCode}")
    public Map<String, Object> getStages(@PathVariable String accessCode) {
        System.out.println("/getStages "+ accessCode);
        return expee.reachableStages(accessCode);
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
