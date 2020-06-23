package com.example.demo.BusinessLayer.Entities.Results;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Map;

@Entity
@Table(name = "code_results")
public class CodeResult extends Result {
    @Lob
    @Column(name = "user_code")
    private String userCode;

    public CodeResult() {
    }

    public CodeResult(String userCode) {
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public Map<String, Object> getAsMap() {
        return Map.of("code", userCode);
    }
}
