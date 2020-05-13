package com.example.demo.RoutingLayer;
import com.example.demo.ServiceLayer.GraderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/grader")
public class GraderRouter {
    @Autowired
    private GraderService grader;

    @RequestMapping("")
    public Map<String, Object> graderLogin(@RequestParam String code) {
        return grader.beginGrading(code);
    }

    // meaningful getters

    @RequestMapping("get_experimentees")
    public Map<String, Object> getExperimentees(@RequestParam String code) {
        return grader.getExperimentees(code);
    }

    @RequestMapping("get_experimentee_results")
    public Map<String, Object> getExperimentees(@RequestParam String code,@RequestParam int participant_id) {
        return grader.getExperimenteeResults(code,participant_id);
    }
}


