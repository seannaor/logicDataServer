package com.example.demo.BusinessLayer;

public class GraderBusiness implements IGraderBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public boolean beginGrading(String accessCode) {
        return cache.getGraderByCode(accessCode)!=null;
    }
}
