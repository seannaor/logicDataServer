package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Exceptions.CodeException;

public class GraderBusiness implements IGraderBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public boolean beginGrading(String accessCode) throws CodeException {
        return cache.getGraderByCode(accessCode)!=null;
    }
}
