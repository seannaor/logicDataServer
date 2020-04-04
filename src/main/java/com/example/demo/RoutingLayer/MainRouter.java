package com.example.demo.RoutingLayer;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.ServiceLayer.CreatorService;
import com.example.demo.ServiceLayer.ExperimenteeService;
import com.example.demo.ServiceLayer.GraderService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MainRouter {

    private CreatorService creator = new CreatorService();
    private GraderService grader = new GraderService();
    private ExperimenteeService expee = new ExperimenteeService();

//    private Map<String, IService> serviceMap = new HashMap<String, IService>() {{
//        put("manager", new CreatorService());
//        put("grader", new GraderService());
//        put("experimentee", new ExperimenteeService());
//    }};

    @PostMapping(value = "/test")
    public String testPage(
            @RequestParam(name = "p1") String abc,
            @RequestParam(name = "p2") int thatIsGay,
            @RequestParam(name = "gay_map") String m
//            @RequestParam(name = "bruh", required = false) String x
    ) {
        String s = "Got the p1=" + abc;
        s += " and got the p2=" + thatIsGay;
        s += "\nDONT FORGET: " + m.toString();
//        s += ".....ANDDDDDDD x=" + x;
        return s;
    }

    @RequestMapping("/grader")
    public Map<String, Object> graderLogin(@RequestParam String code) {
        return grader.beginGrading(code);
    }

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

    @RequestMapping("/manager")
    public Map<String, Object> managerLogin(@RequestParam String username, @RequestParam String password) {
        return creator.researcherLogin(username, password);
    }

    @RequestMapping("/manager/create_exp")
    public Map<String, Object> createExperiment(@RequestParam String username, @RequestParam String exp_name) {
        return creator.createExperiment(username, exp_name);
    }

    @RequestMapping("/manager/add_stage")
    public Map<String, Object> addStage(@RequestParam String username, @RequestParam int exp_id, @RequestParam String stage) {
        return creator.addStageToExperiment(username, exp_id, strToJSON(stage));
    }

    @RequestMapping("/manager/save_exp")
    public Map<String, Object> saveExperiment(@RequestParam String username, @RequestParam int exp_id) {
        return creator.saveExperiment(username, exp_id);
    }

    @RequestMapping("/manager/add_exp")
    public Map<String, Object> addExperiment(@RequestParam String username, @RequestParam String exp_name, @RequestParam List<String> stages) {
        List<JSONObject> jStages = new ArrayList<>();
        for (String stage : stages) {
            jStages.add(strToJSON(stage));
        }
        return creator.addExperiment(username, exp_name, jStages);
    }

    @RequestMapping("/manager/add_grading_task")
    public Map<String, Object> addGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam
            List<String> expee_stages, @RequestParam List<Integer> exp_indexes, @RequestParam List<String> personal_stages) {
        List<JSONObject> jStages_personal = new ArrayList<>();
        for (String stage : personal_stages) {
            jStages_personal.add(strToJSON(stage));
        }
        List<JSONObject> jStages_expee = new ArrayList<>();
        for (String stage : expee_stages) {
            jStages_expee.add(strToJSON(stage));
        }
        return creator.addGradingTask(username, exp_id, task_name, jStages_expee, exp_indexes, jStages_personal);
    }

    @RequestMapping("/manager/add_to_personal")
    public Map<String, Object> addGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam String stage) {
        return creator.addToPersonal(username, exp_id, task_name, strToJSON(stage));
    }

    @RequestMapping("/manager/addToResultsExp")
    public Map<String, Object> addToResultsExp(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam String stage) {
        return creator.addToResultsExp(username, exp_id, task_name, strToJSON(stage));
    }

    @RequestMapping("/manager/setStagesToCheck")
    public Map<String, Object> setStagesToCheck(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam List<Integer> indexes) {
        return creator.setStagesToCheck(username, exp_id, task_name, indexes);
    }

    @RequestMapping("/manager/save_grading_task")
    public Map<String, Object> saveGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name) {
        return creator.saveGradingTask(username, exp_id, task_name);
    }

    @RequestMapping("/manager/add_allie")
    public Map<String, Object> addAllie(@RequestParam String username, @RequestParam int exp_id, @RequestParam String mail, @RequestParam List<String> permissions) {
        return creator.addAllie(username, exp_id, mail, permissions);
    }

    @RequestMapping("/manager/addGraderToTask")
    public Map<String, Object> addGrader(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam String mail) {
        return creator.addGrader(username, exp_id, task_name,mail);
    }

    @RequestMapping("/manager/add_expee")
    public Map<String, Object> addExperimentee(@RequestParam String username, @RequestParam int exp_id, @RequestParam String mail) {
        return creator.addExperimentee(username, exp_id, mail);
    }

    @RequestMapping("/manager/addExpeeToGrader")
    public Map<String, Object> addExpeeToGrader(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam String grader_mail, @RequestParam String expee_mail) {
        return creator.addExpeeToGrader(username, exp_id, task_name,grader_mail,expee_mail);
    }

//    @RequestMapping("/{service}")
//    public Map<String, Object> requestProcessor(@PathVariable String service, @RequestParam String req) {
//        System.out.println("received: " + req);
//        JSONObject jsonReq = strToJSON(req);
//        if (!serviceMap.containsKey(service)) return Map.of("response", "no such entity " + service);
//        return serviceMap.get(service).requestProcessor(jsonReq);
//    }

    private static JSONObject mapToJSON(Map<String, String> req) {
        JSONObject ret = new JSONObject();
        if (req == null) return ret;
        for (String key : req.keySet()) {
            ret.put(key, req.get(key));
        }
        return ret;
    }

    private static JSONObject strToJSON(String req) {
        JSONObject ret = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(req);
        }
        catch (Exception ignore){
            return new JSONObject();
        }
    }

    @Controller
    public class IndexController implements ErrorController {
        @Override
        @RequestMapping("/error")
        @ResponseBody
        public String getErrorPath() {
            // TODO Auto-generated method stub
            return "sorry your request could not be processed :(";
        }

    }

}


