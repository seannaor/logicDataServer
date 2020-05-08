package com.example.demo.BusinessLayer;
import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.data.util.Pair;

public interface IExperimenteeBusiness {

    //UC 2.1 - Login
    Stage beginParticipation(String accessCode) throws ExpEndException, CodeException;

    //UC 2.2.*
    void fillInStage(String accessCode, JSONObject data) throws CodeException, ParseException, ExpEndException, FormatException;

    Stage getNextStage(String accessCode) throws CodeException, ExpEndException;

    Stage getCurrentStage(String accessCode) throws CodeException, ExpEndException;

    Pair<Stage, ResultWrapper> getStage(String code, int id) throws CodeException, NotInReachException;
}
