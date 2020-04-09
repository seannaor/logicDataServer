package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.GraderBusiness;
import com.example.demo.BusinessLayer.IGraderBusiness;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GraderService {

    private IGraderBusiness graderBusiness = new GraderBusiness();

    public Map<String,Object> beginGrading(String code){
        String res = "OK";
        try{
            graderBusiness.beginGrading(code);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }
}
