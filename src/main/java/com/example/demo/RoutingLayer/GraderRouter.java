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

import static com.example.demo.RoutingLayer.RouterUtils.strToJSON;

@RestController
@RequestMapping("/grader")
public class GraderRouter {

    private GraderService grader = new GraderService();

    @RequestMapping("")
    public Map<String, Object> graderLogin(@RequestParam String code) {
        return grader.beginGrading(code);
    }

}


