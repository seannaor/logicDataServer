package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.GraderBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class GraderService {
    @Autowired
    private GraderBusiness graderBusiness;

//    public GraderService() {
//        this.graderBusiness = new GraderBusiness();
//    }

    public Map<String, Object> beginGrading(String code) {
        String res = "OK";
        try {
            graderBusiness.beginGrading(code);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }
}
