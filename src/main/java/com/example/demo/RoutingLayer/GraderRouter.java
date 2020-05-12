package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.GraderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.decode;
import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;

@RestController
@RequestMapping("/grader")
public class GraderRouter {
    @Autowired
    private GraderService grader;

    @RequestMapping("")
    public Map<String, Object> graderLogin(@RequestParam String code) {
        return grader.beginGrading(code);
    }

//    @RequestMapping("/fill_stage")
//    public Map<String, Object> fillStage(@RequestParam String code, @RequestParam int task_id, @RequestParam String data) {
//        data = decode(data);
//        System.out.println("experimentee/fill_stage " + code);
//        return grader.fillInStage(code, task_id, strToJSON(data));
//    }
//
//    @RequestMapping("/next_stage")
//    public Map<String, Object> nextStage(@RequestParam String code, @RequestParam int task_id) {
//        System.out.println("experimentee/next_stage " + code);
//        return grader.getNextStage(code, task_id);
//    }
//
//    @RequestMapping("/current_stage")
//    public Map<String, Object> currStage(@RequestParam String code, @RequestParam int task_id) {
//        System.out.println("experimentee/current_stage " + code);
//        return grader.getCurrentStage(code, task_id);
//    }
//
//    @RequestMapping("/get_stage/{id}")
//    public Map<String, Object> getStageAt(@PathVariable("id") int id, @RequestParam String code, @RequestParam int task_id) {
//        System.out.println("experimentee/get_stage " + id + " " + code);
//        return grader.getStageAt(code, task_id, id);
//    }

    // meaningful getters
}


