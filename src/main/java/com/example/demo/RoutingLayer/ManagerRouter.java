package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.CreatorService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/admin")
public class ManagerRouter {
    @Autowired
    private CreatorService creator;

    @PostMapping("/login")
    public boolean managerLogin(@RequestBody Map<String, String> credentials){
        String username = credentials.get("username"),
                password = credentials.get("password");
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
    public Map<String, Object> addGraderToGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String mail) {
        return creator.addGraderToGradingTask(username, exp_id, task_id, mail);
    }

    @RequestMapping("/add_expee")
    public Map<String, Object> addExperimentee(@RequestParam String username, @RequestParam int exp_id, @RequestParam String mail) {
        return creator.addExperimentee(username, exp_id, mail);
    }

    @RequestMapping("/addExpeeToGrader")
    public Map<String, Object> addExpeeToGrader(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String grader_mail, @RequestParam String expee_mail) {
        return creator.addExpeeToGrader(username, exp_id, task_id, grader_mail, expee_mail);
    }

    // meaningful getters

    @RequestMapping("/get_experiments")
    public Map<String, Object> getExperiments(@RequestParam String username) {
        return creator.getExperiments(username);
    }

    @RequestMapping("/get_allies")
    public Map<String, Object> getAllies(@RequestParam String username, @RequestParam int exp_id) {
        return creator.getAllies(username, exp_id);
    }

    @RequestMapping("/get_stages")
    public Map<String, Object> getStages(@RequestParam String username, @RequestParam int exp_id) {
        return creator.getStages(username, exp_id);
    }

    @RequestMapping("/get_experimentees")
    public Map<String, Object> getExperimentees(@RequestParam String username, @RequestParam int exp_id) {
        return creator.getExperimentees(username, exp_id);
    }

    @RequestMapping("/get_grading_task")
    public Map<String, Object> getGradingTasks(@RequestParam String username, @RequestParam int exp_id) {
        return creator.getGradingTasks(username, exp_id);
    }

    @RequestMapping("/get_personal_stages")
    public Map<String, Object> getPersonalStages(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id) {
        return creator.getPersonalStages(username, exp_id, task_id);
    }
    
    @RequestMapping("/get_evaluation_stages")
    public Map<String, Object> getEvaluationStages(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id) {
        return creator.getEvaluationStages(username, exp_id, task_id);
    }

    @RequestMapping("/get_task_graders")
    public Map<String, Object> getTaskGraders(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id) {
        return creator.getTaskGraders(username, exp_id, task_id);
    }

    @RequestMapping("/get_task_experimentees")
    public Map<String, Object> getTaskExperimentees(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id) {
        return creator.getTaskExperimentees(username, exp_id, task_id);
    }
}
