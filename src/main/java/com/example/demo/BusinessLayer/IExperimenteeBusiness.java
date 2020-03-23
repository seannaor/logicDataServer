package com.example.demo.BusinessLayer;
import org.json.simple.JSONObject;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    boolean beginParticipation(String accessCode);

    //UC 2.2.*
    String fillInStage(String accessCode, JSONObject data);
}
