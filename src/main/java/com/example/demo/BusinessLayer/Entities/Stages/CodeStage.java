package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.*;

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

    public CodeStage(String desc, String template, Experiment experiment) {
        super(experiment);
        this.description = desc;
        this.template = template;
    }

    public CodeStage(String desc, String template, List<String> requirements, Experiment experiment) {
        super(experiment);
        this.description = desc;
        this.template = template;
        this.requirements = new ArrayList<>();
        for (String req : requirements) {
            this.requirements.add(new Requirement(this, req));
        }
    }

    public CodeStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    @Override
    public Map<String, Object> getAsMap() {
        Map<String, Object> stageMap = new HashMap<>();
        stageMap.put("description", this.description);
        stageMap.put("template", this.template);
        stageMap.put("language", "None");
        List<String> requirements = new LinkedList<>();
        for (Requirement r : this.requirements) {
            requirements.add(r.getText());
        }
        stageMap.put("requirements", requirements);
        return stageMap;
    }

    @Override
    public String getType() {
        return "code";
    }

    @Override
    public CodeResult fillCode(Map<String, Object> data, Participant participant) throws FormatException {
        if (!data.containsKey("userCode")) throw new FormatException("user code");
        return new CodeResult(participant, this, (String) data.get("userCode"));
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
