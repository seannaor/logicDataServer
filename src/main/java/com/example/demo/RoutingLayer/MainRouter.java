package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.CreatorService;
import com.example.demo.ServiceLayer.ExperimenteeService;
import com.example.demo.ServiceLayer.GraderService;
import com.example.demo.ServiceLayer.IService;
import org.json.simple.JSONObject;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MainRouter {

    private Map<String, IService> serviceMap = new HashMap<String, IService>() {{
        put("manager", new CreatorService());
        put("grader", new GraderService());
        put("experimentee", new ExperimenteeService());
    }};

    @RequestMapping("/{service}")
    public Map<String, Object> requestProcessor(@PathVariable String service, @RequestParam Map<String, String> req) {
        System.out.println("received: " + req);
        JSONObject jsonReq = mapToJSON(req);
        if (!serviceMap.containsKey(service)) return Map.of("response", "no such entity " + service);
        return serviceMap.get(service).requestProcessor(jsonReq);
    }

    private static JSONObject mapToJSON(Map<String, String> req) {
        JSONObject ret = new JSONObject();
        if (req == null) return ret;
        for (String key : req.keySet()) {
            ret.put(key, req.get(key));
        }
        return ret;
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


