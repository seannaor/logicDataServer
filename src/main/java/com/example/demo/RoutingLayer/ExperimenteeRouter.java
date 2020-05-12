package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.decode;
import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;

@RestController
@RequestMapping("/experimentee")
public class ExperimenteeRouter {
    @Autowired
    private ExperimenteeService expee;

    @RequestMapping("")
    public Map<String, Object> expeeLogin(@RequestParam String code) {
        System.out.println("/experimentee " + code);
        return expee.beginParticipation(code);
    }

    @RequestMapping("/fill_stage")
    public Map<String, Object> fillStage(@RequestParam String code, @RequestParam String data) {
        data = decode(data);
        System.out.println("experimentee/fill_stage " + code);
        return expee.fillInStage(code, strToJSON(data));
    }

    @RequestMapping("/next_stage")
    public Map<String, Object> nextStage(@RequestParam String code) {
        System.out.println("experimentee/next_stage " + code);
        return expee.getNextStage(code);
    }

    @RequestMapping("/current_stage")
    public Map<String, Object> currStage(@RequestParam String code) {
        System.out.println("experimentee/current_stage " + code);
        return expee.getCurrentStage(code);
    }

    @RequestMapping("/get_stage/{id}")
    public  Map<String, Object> getStageAt(@PathVariable("id") int id, @RequestParam String code) {
        System.out.println("experimentee/get_stage "+id+" " + code);
        return expee.getStageAt(code,id);
    }

}
