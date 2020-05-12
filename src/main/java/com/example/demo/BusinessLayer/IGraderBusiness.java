package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;

import javax.xml.transform.Result;
import java.util.List;

public interface IGraderBusiness {
    //UC - 3.1 - Login
    boolean beginGrading(String accessCode);

    List<Integer> getParticipantsByTask(String accessCode) throws CodeException;

    List<ResultWrapper> getExpeeRes(String accessCode, int parti_code) throws CodeException, NotExistException, FormatException;
}
