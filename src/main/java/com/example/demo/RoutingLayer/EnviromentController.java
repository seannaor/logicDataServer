package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.ExperimenteeService;
import org.asynchttpclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class EnviromentController {

    private ExperimenteeService expee;
    private final RestTemplate restTemplate;
    private final String tokenUrl = "https://judge0.p.rapidapi.com/";
    private final String judge0Key = "460509d2cemsh25d3565f9399f0bp1004c7jsne3f4ac235341";
    private HttpHeaders headers;
    AsyncHttpClient client = Dsl.asyncHttpClient();
    DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config();
//    = new HttpHeaders({
//        "X-RapidAPI-Host": "judge0.p.rapidapi.com",
//                "X-RapidAPI-Key": judge0Key
//    });

    @Autowired
    public EnviromentController(ExperimenteeService expee, RestTemplateBuilder restTemplateBuilder) {
        this.expee = expee;
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostMapping("/login")
    public boolean logIn(@RequestBody String accessCode) {
        System.out.println("/login " + accessCode);
        return expee.beginParticipation(accessCode);
    }

    @GetMapping("/getStages/{accessCode}")
    public Map<String, Object> getStages(@PathVariable String accessCode) {
        System.out.println("/getStages " + accessCode);
        return expee.reachableStages(accessCode);
    }

    @PostMapping("/submitStage/{accessCode}")
    public Map<String, Object> submitStage(@PathVariable String accessCode,
                                           @RequestBody Map<String, Object> stageInfo) {
        System.out.println("/submitStage " + accessCode + " data " + stageInfo);
        return expee.fillInStage(accessCode, stageInfo);
    }

    @GetMapping("/getLanguages")
    public String getLanguages() {
        String url = tokenUrl + "languages";
        Request request = Dsl.get(url).build();

        ListenableFuture<Response> listenableFuture = client
                .executeRequest(request);
        listenableFuture.addListener(() -> {
            try {
                Response response = listenableFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }, Executors.newCachedThreadPool());

        try {
            Response response = listenableFuture.get();
            return response.getContentType() + '\n' + response.getResponseBody() + '\n' + response.getStatusText();
        } catch (InterruptedException | ExecutionException ignore) {
        }
        return "Oops, something went wrong...";
//        return Map.of("Error","Oops, something went wrong...");
    }

//    @RequestMapping("/next_stage")
//    public Map<String, Object> nextStage(@RequestParam String code) {
//        System.out.println("experimentee/next_stage " + code);
//        return expee.getNextStage(code);
//    }
//
//    @RequestMapping("/current_stage")
//    public Map<String, Object> currStage(@RequestParam String code) {
//        System.out.println("experimentee/current_stage " + code);
//        return expee.getCurrentStage(code);
//    }
//
//    @RequestMapping("/get_stage/{id}")
//    public  Map<String, Object> getStageAt(
//            @PathVariable("id") int id, @RequestParam String code) {
//        System.out.println("experimentee/get_stage "+id+" " + code);
//        return expee.getStageAt(code,id);
//    }
}