package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
    @Column(name = "language")
    private String language;
    @OneToMany(mappedBy = "codeStage")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Requirement> requirements = new ArrayList<>();


    public CodeStage() {
    }

    public CodeStage(String desc, String template, List<String> requirements, String language) {
        this.description = desc;
        this.template = template;
        this.language = language;
        this.requirements = new ArrayList<>();
        int reqIdx = 0;
        for (String req : requirements) {
            this.requirements.add(buildRequirement(req, reqIdx++));
        }
    }

    private Requirement buildRequirement(String req, int idx) {
        Requirement newRequirement = new Requirement(req);
        newRequirement.setRequirementIndex(idx);
        newRequirement.setCodeStage(this);
        return newRequirement;
    }


    @Override
    public void setExperiment(Experiment experiment) {
        super.setExperiment(experiment);
        for (Requirement req : this.requirements) {
            req.setStageIndex(this.getStageID().getStageIndex());
            req.setExperimentId(experiment.getExperimentId());
        }
    }


    // if old is null, new CodeResult will be created, else, old will be chanced
    @Override
    public CodeResult fillCode(Map<String, Object> data, CodeResult old) throws FormatException {
        String code = validate(data);
        if (old == null) old = new CodeResult(code);
        else old.setUserCode(code);
        return old; // old is actually new :)
    }

    private String validate(Map<String, Object> data) throws FormatException {
        try {
            String code = (String) data.get("code");
            if (code != null) return code;
        } catch (Exception ignored) {
        }
        throw new FormatException("user code");
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

    // getters
    public String getLanguage() {
        return language;
    }

    // setters
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public Map<String, Object> getAsMap() {
        Map<String, Object> stageMap = new HashMap<>();
        stageMap.put("description", this.description);
        stageMap.put("template", this.template);
        stageMap.put("language", this.language);
        List<String> requirements = new LinkedList<>();
        for (Requirement r : this.requirements) {
            requirements.add(r.getText());
        }
        stageMap.put("requirements", requirements);
        return Map.of("type", getType(), "stage", stageMap);
    }

    @Override
    public String getType() {
        return "code";
    }

}
