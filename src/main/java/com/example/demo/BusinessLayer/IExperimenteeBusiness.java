package com.example.demo.BusinessLayer;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import org.json.simple.JSONObject;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    Stage beginParticipation(String accessCode) throws Exception;

    //UC 2.2.*
    void fillInStage(String accessCode, JSONObject data) throws Exception;

    Stage getNextStage(String accessCode) throws Exception;

    Stage getCurrentStage(String accessCode) throws Exception;
}
