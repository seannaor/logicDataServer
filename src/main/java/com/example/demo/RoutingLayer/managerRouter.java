package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.CreatorService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.*;

@RestController
@RequestMapping("/manager")
public class managerRouter {

    private CreatorService creator;

    public managerRouter() {
        this.creator = new CreatorService();
    }

    @RequestMapping("")
    public Map<String, Object> managerLogin(@RequestParam String username, @RequestParam String password) {
        return creator.researcherLogin(username, password);
    }

    @RequestMapping("/create_exp")
    public Map<String, Object> createExperiment(@RequestParam String username, @RequestParam String exp_name) {
        System.out.println(username + " " + exp_name);
        return creator.createExperiment(username, exp_name);
    }

    @RequestMapping("/add_stage")
    public Map<String, Object> addStage(@RequestParam String username, @RequestParam int exp_id, @RequestParam String stage) {
        stage = decode(stage);
        System.out.println(stage);
        return creator.addStageToExperiment(username, exp_id, strToJSON(stage));
    }

    @RequestMapping("/save_exp")
    public Map<String, Object> saveExperiment(@RequestParam String username, @RequestParam int exp_id) {
        return creator.saveExperiment(username, exp_id);
    }

    @RequestMapping("/add_exp")
    public Map<String, Object> addExperiment(@RequestParam String username, @RequestParam String exp_name, @RequestParam List<String> stages) {
        List<JSONObject> jStages = new ArrayList<>();
        for (String stage : stages) {
            stage = decode(stage);
            jStages.add(strToJSON(stage));
        }
        return creator.addExperiment(username, exp_name, jStages);
    }

    @RequestMapping("/add_grading_task")
    public Map<String, Object> addGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam
            List<String> expee_stages, @RequestParam List<Integer> exp_indexes, @RequestParam List<String> personal_stages) {
        List<JSONObject> jStages_personal = new ArrayList<>();
        for (String stage : personal_stages) {
            stage = decode(stage);
            jStages_personal.add(strToJSON(stage));
        }
        List<JSONObject> jStages_expee = new ArrayList<>();
        for (String stage : expee_stages) {
            stage = decode(stage);
            jStages_expee.add(strToJSON(stage));
        }
        return creator.addGradingTask(username, exp_id, task_name, jStages_expee, exp_indexes, jStages_personal);
    }

    @RequestMapping("/add_to_personal")
    public Map<String, Object> addGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String stage) {
        stage = decode(stage);
        return creator.addToPersonal(username, exp_id, task_id, strToJSON(stage));
    }

    @RequestMapping("/addToResultsExp")
    public Map<String, Object> addToResultsExp(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String stage) {
        stage = decode(stage);
        return creator.addToResultsExp(username, exp_id, task_id, strToJSON(stage));
    }

    @RequestMapping("/setStagesToCheck")
    public Map<String, Object> setStagesToCheck(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam List<Integer> indexes) {
        return creator.setStagesToCheck(username, exp_id, task_id, indexes);
    }

    @RequestMapping("/save_grading_task")
    public Map<String, Object> saveGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name) {
        return creator.saveGradingTask(username, exp_id, task_name);
    }

    @RequestMapping("/add_allie")
    public Map<String, Object> addAllie(@RequestParam String username, @RequestParam int exp_id, @RequestParam String mail, @RequestParam String role, @RequestParam List<String> permissions) {
        return creator.setAlliePermissions(username, exp_id, mail, role, permissions);
    }

    @RequestMapping("/addGraderToTask")
    public Map<String, Object> addGrader(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String mail) {
        return creator.addGrader(username, exp_id, task_id, mail);
    }

    @RequestMapping("/add_expee")
    public Map<String, Object> addExperimentee(@RequestParam String username, @RequestParam int exp_id, @RequestParam String mail) {
        return creator.addExperimentee(username, exp_id, mail);
    }

    @RequestMapping("/addExpeeToGrader")
    public Map<String, Object> addExpeeToGrader(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String grader_mail, @RequestParam String expee_mail) {
        return creator.addExpeeToGrader(username, exp_id, task_id, grader_mail, expee_mail);
    }

}
