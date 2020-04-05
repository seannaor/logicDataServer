package com.example.demo.BusinessLayer;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import org.json.simple.JSONObject;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    String beginParticipation(String accessCode);

    //UC 2.2.*
    String fillInStage(String accessCode, JSONObject data);

    Stage getNextStage(String accessCode);

    Stage getCurrentStage(String accessCode);
}
