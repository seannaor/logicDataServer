package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Participant;
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

    public CodeStage(String desc, String template, String language, Experiment experiment) {
        super(experiment);
        this.description = desc;
        this.template = template;
        this.language = language;
    }

    public CodeStage(String desc, String template, List<String> requirements, String language, Experiment experiment) {
        super(experiment);
        this.description = desc;
        this.template = template;
        this.language = language;
        this.requirements = new ArrayList<>();
        for (String req : requirements) {
            this.requirements.add(new Requirement(this, req));
        }
    }

    public CodeStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    @Override
    public CodeResult fillCode(Map<String,Object> data, Participant participant) throws FormatException {
        String code;
        try{
            code = (String) data.get("code");
            if (code == null)
                throw new FormatException("user code");
        }catch (Exception e) {
            throw new FormatException("user code");
        }
        return new CodeResult(participant, this, code);
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

    // setters
    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    // getters
    public String getLanguage() {
        return language;
    }

    public String getTemplate() {
        return template;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Requirement> getRequirements() {
        return requirements;
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
        return stageMap;
    }

    @Override
    public String getType() {
        return "code";
    }
    
}
