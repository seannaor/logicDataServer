package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;

import java.util.List;
import java.util.UUID;

public interface IGraderBusiness {

    //UC - 3.1 - Login
    boolean beginGrading(UUID accessCode);

    List<Participant> getParticipantsByTask(UUID accessCode) throws CodeException;

    List<ResultWrapper> getExpeeRes(UUID accessCode, int parti_code) throws CodeException, NotExistException, FormatException;
}
