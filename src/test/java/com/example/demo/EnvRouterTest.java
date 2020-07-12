package com.example.demo;

import com.example.demo.RoutingLayer.EnviromentController;
import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Future;

@SpringBootTest
public class EnvRouterTest {

    @Autowired
    private EnviromentController env;
    private final String tokenUrl = "https://judge0.p.rapidapi.com/languages";
    private final String judge0Key = "460509d2cemsh25d3565f9399f0bp1004c7jsne3f4ac235341";

    @Test
    public void test1()  {
        Map<String,Object> l = env.getLanguages();
        Assert.assertFalse(l.isEmpty());
    }

    @Test
    public void test2() throws InterruptedException {
        Future<HttpResponse<com.mashape.unirest.http.JsonNode>> future = Unirest.get(tokenUrl)
                .header("X-RapidAPI-Key", judge0Key)
                .asJsonAsync(new Callback<JsonNode>() {

                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                        JsonNode body = response.getBody();
                        System.out.println("in");
                        System.out.println(body);
                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });
        System.out.println("out");
        System.out.println("done");
    }

    @Test
    public void test3()  {
        System.out.println(env.runCode("print('hello')","Python (3.8.1)"));
    }
}
