package com.example.demo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "questionnaire_stages")
public class QuestionnaireStage extends Stage{
//    @MapsId("grading_task_id")
//    @JoinColumn(name = "grading_task_id", referencedColumnName = "grading_task_id")
//    @OneToOne
//    private com.example.demo.GradingTask gradingTask;

    public QuestionnaireStage() {
    }
}