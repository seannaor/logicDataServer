package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import org.json.simple.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "info_stages")
public class InfoStage extends Stage {
    @Lob
    @Column(name = "info")
    private String info;

    public InfoStage() {
    }

    public InfoStage(String info) {
        this.info = info;
    }

    public InfoStage(String info, Experiment experiment) {
        super(experiment);
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public JSONObject getJson(){
        JSONObject jStage = new JSONObject();
        jStage.put("type","info");
        jStage.put("info",info);
        return jStage;
    }

    @Override
    public String getType() {
        return "info";
    }

    @Override
    public void fillInfo(JSONObject data) {}
}