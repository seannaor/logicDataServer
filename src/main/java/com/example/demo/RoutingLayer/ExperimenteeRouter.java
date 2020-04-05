package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;

@RestController
@RequestMapping("/experimentee")
public class ExperimenteeRouter {

    private ExperimenteeService expee = new ExperimenteeService();
    private int count =0;

    @RequestMapping("")
    public Map<String, Object> expeeLogin(@RequestParam String code) {
        System.out.println("code: "+code);
        //return expee.beginParticipation(code);
        return Map.of("response","info");
    }

    @RequestMapping("/fill_stage")
    public Map<String, Object> expeeLogin(@RequestParam String code, @RequestParam String data) {
        System.out.println(data);
        return expee.fillInStage(code, strToJSON(data));
    }

    @RequestMapping("/next_stage")
    public Map<String, Object> nextStage(@RequestParam String code) {
        if(count==0) {
            count++;
            List<String> requirements = new ArrayList<>();
            requirements.add("do that");
            requirements.add("do this");
            requirements.add("fuck off");
            return Map.of("type", "code", "description", "design me a system that can create & manage & run " +
                    "experiments", "template", "return null;", "requirements", requirements,"next","goodbye");
        }
        else if(count==1){
            count++;
            List<JSONObject> questions = new ArrayList<>();
            JSONObject q1 = new JSONObject();
            q1.put("type", "open");
            q1.put("question", "WTF?!?");
            questions.add(q1);
            JSONObject q2 = new JSONObject();
            q2.put("type", "multi");
            q2.put("question", "who?");
            List<String> answers = new ArrayList<>();
            answers.add("me");
            answers.add("you");
            answers.add("no one");
            answers.add("we both");
            q2.put("answers", answers);
            questions.add(q2);
            return Map.of("type", "questionnaire","questions", questions);
        }return Map.of("type","goodbye");
//        return expee.getNextStage(code);
    }

    @RequestMapping("/current_stage")
    public Map<String, Object> currStage(@RequestParam String code) {
//        return expee.getCurrentStage(code);
        //{"ok":"1","username": "shahr", "msg" : "this is a long and random text thta will be presented as some kind of an infpo page" }
        return Map.of("type", "info","info", "fuck all gilis","next","code");
    }

}
