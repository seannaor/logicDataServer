package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.RoutingLayer.RouterUtils.decode;
import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/admin")
public class ManagerController {

    private CreatorService creator;

    @Autowired
    public ManagerController(CreatorService creator) {
        this.creator = creator;
    }

    @PostMapping("/login")
    public boolean managerLogin(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username"),
                password = credentials.get("password");
        return creator.researcherLogin(username, password);
    }

    @PostMapping("/addExperiment/{username}")
    public Map<String, Object> addExperiment(@PathVariable String username,
                                             @RequestBody Map<String, Object> experiment) {
        System.out.println("/addExperiment " + username);
        String experimnetnName = (String) experiment.get("expName");
        List<Map<String, Object>> stages = (List<Map<String, Object>>) experiment.get("stages");
        return creator.addExperiment(username, experimnetnName, stages);
    }

    @PostMapping("/addExperimentees/{username}/{expName}")
    public Map<String, Object> addExperimentees(@PathVariable String username, @PathVariable String expName,
                                                @RequestBody Map<String, List<String>> eMails) {
        System.out.println("/add_experimentees " + username);
        List<String> emails = (List<String>) eMails.get("emails");
        return creator.addExperimentees(username, expName, emails);
    }

    @GetMapping("/getExperimentees/{username}/{expName}")
    public Map<String, Object> getExperimentees(@PathVariable String username, @PathVariable String expName) {
        System.out.println("/getExperimentees " + username + " exp " + expName);
        return creator.getExperimentees(username, expName);
    }

    @GetMapping("/getExperiments/{username}")
    public Map<String, Object> getExperiments(@PathVariable String username) {
        return creator.getExperiments(username);
    }


    @PostMapping("/addAlly/{adminUsername}")
    public Map<String, Object> addAlly(@PathVariable String adminUsername,
                                       @RequestBody Map<String, Object> allyDetails) {
        String password = (String) allyDetails.get("password");
        String email = (String) allyDetails.get("email");
        return creator.addAlly(adminUsername, email, password);
    }

    @PostMapping("/addAlly/{username}/{expName}/{mail}/{password}")
    public Map<String, Object> addAlly(@PathVariable String username, @PathVariable String expName, @PathVariable String mail, @PathVariable String password) {
        return creator.addAllyToExp(username, expName, mail, password);
    }

    // old version V

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

    @RequestMapping("/add_grading_task")
    public Map<String, Object> addGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam String task_name, @RequestParam
            List<String> expee_stages, @RequestParam List<Integer> exp_indexes, @RequestParam List<String> personal_stages) {
        List<Map<String, Object>> jStages_personal = new ArrayList<>();
        for (String stage : personal_stages) {
            stage = decode(stage);
            jStages_personal.add(strToJSON(stage));
        }
        List<Map<String, Object>> jStages_expee = new ArrayList<>();
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

    @RequestMapping("/addGraderToTask")
    public Map<String, Object> addGraderToGradingTask(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String mail) {
        return creator.addGraderToGradingTask(username, exp_id, task_id, mail);
    }

    @RequestMapping("/addExpeeToGrader")
    public Map<String, Object> addExpeeToGrader(@RequestParam String username, @RequestParam int exp_id, @RequestParam int task_id, @RequestParam String grader_mail, @RequestParam String expee_mail) {
        return creator.addExpeeToGrader(username, exp_id, task_id, grader_mail, expee_mail);
    }

    // meaningful getters

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


//    @RequestMapping("/add_exp")
//    public Map<String, Object> addExperiment(@RequestParam String username, @RequestParam String exp_name, @RequestParam List<String> stages) {
//        List<JSONObject> jStages = new ArrayList<>();
//        for (String stage : stages) {
//            stage = decode(stage);
//            jStages.add(strToJSON(stage));
//        }
//        return creator.addExperiment(username, exp_name, jStages);
//    }
}