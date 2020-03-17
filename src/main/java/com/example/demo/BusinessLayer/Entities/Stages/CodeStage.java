package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "code_stages")
public class CodeStage extends Stage {

    @Lob
    private String description;
    @Lob
    private String template;

    public CodeStage() {
    }

    public CodeStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public CodeStage(String desc, String template, List<String> requirements, Experiment experiment) {
        super(experiment);
        this.description=desc;
        this.template=template;
        //TODO: add requirements field - this.requirements = requirements;
    }

    public String getDescription() {
        return description;
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
}
