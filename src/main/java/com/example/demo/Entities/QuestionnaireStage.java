package com.example.demo.Entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage {
    public QuestionnaireStage() {
        super();
    }

    public QuestionnaireStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }
}
