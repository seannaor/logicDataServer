package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Grader;

import java.util.ArrayList;
import java.util.List;

public class GraderBusiness implements IGraderBusiness {

    private List<Grader> graders=new ArrayList<>();

    @Override
    public boolean beginGrading(String accessCode) {
        return false;
    }
}
