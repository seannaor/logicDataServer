package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

@Sql({"/create_database.sql"})
@SpringBootTest
public class CodeUnitTest {
    private CodeStage codeStage;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException {
        Experiment exp = new Experiment("Experiment Name");
        exp.setExperimentId(100);
        codeStage = new CodeStage("make me hello world program","//write code here","JAVA",exp);
    }

    @Test
    public void settersGetters(){
        String desc = "no description",temp = "//comment",lang = "SIC";
        codeStage.setDescription(desc);
        codeStage.setTemplate(temp);
        codeStage.setLanguage(lang);

        Assert.assertEquals(desc,codeStage.getDescription());
        Assert.assertEquals(temp,codeStage.getTemplate());
        Assert.assertEquals(lang,codeStage.getLanguage());
    }


}
