package com.example.demo.UnitTests.StagesUnitTests;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StageUnitTest {

    @Test
    public void FailParseStage() {
        assertThrows(FormatException.class, () -> {
            JSONObject json = new JSONObject();
            json.put("info", "some information!");
            Stage.parseStage(json, null);
        });
    }
}
