package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "code_stages")
public class CodeStage extends Stage {

    @Lob
    @Column(name = "description")
    private String description;
    @Lob
    @Column(name = "template")
    private String template;
    @OneToMany(mappedBy = "codeStage")
    private List<Requirement> requirements = new ArrayList<>();

    public CodeStage() {
    }

    public CodeStage(String desc, String template, List<String> requirements, Experiment experiment) {
        super(experiment);
        this.description=desc;
        this.template=template;
        //TODO: add requirements field - this.requirements = requirements;
    }

    public CodeStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public JSONObject getJson(){
        JSONObject jStage = new JSONObject();
        jStage.put("type","code");
        jStage.put("description",this.description);
        jStage.put("template",this.template);
        List<String> jRequirements = new LinkedList<>();
        for (Requirement r : requirements) {
            jRequirements.add(r.getText());
        }
        jStage.put("requirements",jRequirements);
        return jStage;
    }

    public CodeStage(String desc, String template, Experiment experiment) {
        super(experiment);
        this.description=desc;
        this.template=template;
    }

    public String getDescription() {
        return description;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

}
