package com.example.demo.BusinessLayer;
import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.UUID;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    Stage beginParticipation(UUID accessCode) throws ExpEndException, CodeException;

    //UC 2.2.*
    void fillInStage(UUID accessCode, JSONObject data) throws CodeException, ParseException, ExpEndException, FormatException;

    Stage getNextStage(UUID accessCode) throws CodeException, ExpEndException;

    Stage getCurrentStage(UUID accessCode) throws CodeException, ExpEndException;

    Stage getStage(UUID code, int id) throws CodeException, NotInReachException;

    ResultWrapper getResult(UUID accessCode, int idx) throws CodeException, NotInReachException;
}
