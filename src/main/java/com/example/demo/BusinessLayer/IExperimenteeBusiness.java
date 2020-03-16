package com.example.demo.BusinessLayer;

import net.minidev.json.JSONObject;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    boolean beginParticipation(String accessCode);

    //UC 2.2.*
    boolean fillInStage(String accessCode, JSONObject data);
}
