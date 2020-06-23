package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.util.UUID;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    boolean beginParticipation(UUID accessCode) throws ExpEndException, CodeException, NotExistException;

    //UC 2.2.*
    void fillInStage(UUID accessCode, Map<String, Object> data) throws CodeException, ParseException, ExpEndException, FormatException, NotInReachException, NotExistException;

    Stage getNextStage(UUID accessCode) throws CodeException, ExpEndException, NotExistException;

    Stage getCurrentStage(UUID accessCode) throws CodeException, ExpEndException, NotExistException;

    Stage getStage(UUID code, int id) throws CodeException, NotInReachException, NotExistException;

    Result getResult(UUID accessCode, int idx) throws CodeException, NotInReachException;
}
