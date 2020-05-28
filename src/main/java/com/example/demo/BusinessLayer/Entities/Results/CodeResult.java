package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "code_results")
public class CodeResult extends Result {
    @Lob
    @Column(name = "user_code")
    private String userCode;

    public CodeResult() { }

    public CodeResult(Participant participant, CodeStage codeStage, String userCode) {
        super(codeStage, participant);
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public Map<String,Object> getAsMap() {
        return Map.of("code",userCode);
    }
}
