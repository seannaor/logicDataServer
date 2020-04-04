package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;

public class ExperimenteeRouter {

    private ExperimenteeService expee = new ExperimenteeService();

    @RequestMapping("/experimentee")
    public Map<String, Object> expeeLogin(@RequestParam String code) {
        System.out.println("code: "+code);
        return expee.beginParticipation(code);
    }

    @RequestMapping("/experimentee/fill_stage")
    public Map<String, Object> expeeLogin(@RequestParam String code, @RequestParam String data) {
        return expee.fillInStage(code, strToJSON(data));
    }

    @RequestMapping("/experimentee/next_stage")
    public Map<String, Object> nextStage(@RequestParam String code) {
        return expee.getNextStage(code);
    }

    @RequestMapping("/experimentee/current_stage")
    public Map<String, Object> currStage(@RequestParam String code) {
        return expee.getCurrentStage(code);
    }

}
