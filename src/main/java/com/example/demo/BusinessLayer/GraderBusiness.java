package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Exceptions.CodeException;

public class GraderBusiness implements IGraderBusiness {

    private DataCache cache;

    public GraderBusiness() {
        this.cache = DataCache.getInstance();
    }

    @Override
    public boolean beginGrading(String accessCode) {
        try {
            cache.getGraderByCode(accessCode);
            return true;
        } catch (CodeException ignore) {
            return false;
        }
    }
}
