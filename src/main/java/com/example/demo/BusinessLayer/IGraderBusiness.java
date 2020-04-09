package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Exceptions.CodeException;

public interface IGraderBusiness {
    //UC - 3.1 - Login
    boolean beginGrading(String accessCode) throws CodeException;
}
