package com.example.demo.UnitTests.ResultsUnitTests;

import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;


public class CodeResultUnitTests {

    private CodeResult codeResult = new CodeResult("return 'nice';");

    @Test
    public void setGetTest(){
        String code = "//code here";
        codeResult.setUserCode(code);
        Assert.assertEquals(code,codeResult.getUserCode());
    }

    @Test
    public void getMapTest(){
        String code = "//code here";
        codeResult.setUserCode(code);
        Map<String,Object> map = codeResult.getAsMap();
        Assert.assertTrue(map.containsKey("code"));
        Assert.assertEquals(map.get("code"), code);
    }
}
