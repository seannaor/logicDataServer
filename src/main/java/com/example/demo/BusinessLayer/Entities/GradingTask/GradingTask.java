package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grading_tasks")
public class GradingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grading_task_id")
    private int gradingTaskId;
    @Column(name = "grading_task_name")
    private String gradingTaskName;
    @ManyToOne
    @JoinColumn(name = "base_experiment", referencedColumnName = "experiment_id")
    private Experiment baseExperiment;
    @OneToOne
    @JoinColumn(name = "general_experiment", referencedColumnName = "experiment_id")
    private Experiment generalExperiment;
    @OneToOne
    @JoinColumn(name = "grading_experiment", referencedColumnName = "experiment_id")
    private Experiment gradingExperiment;
    @OneToMany(mappedBy = "gradingTask")
    private List<GraderToGradingTask> assignedGradingTasks = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "stages_of_grading_task",
            joinColumns = {
                    @JoinColumn(name = "grading_task_id", referencedColumnName = "grading_task_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
                    @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")}
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Stage> stages;

    public GradingTask() { }

    public GradingTask(String gradingTaskName, Experiment baseExperiment, Experiment generalExperiment, Experiment gradingExperiment) {
        this.gradingTaskName = gradingTaskName;
        this.baseExperiment = baseExperiment;
        this.generalExperiment = generalExperiment;
        this.gradingExperiment = gradingExperiment;
        this.stages =new ArrayList<>();
    }

    public GradingTask(String gradingTaskName, Experiment baseExperiment, Experiment generalExperiment, Experiment gradingExperiment, List<Stage> stages) {
        this.gradingTaskName = gradingTaskName;
        this.baseExperiment = baseExperiment;
        this.generalExperiment = generalExperiment;
        this.gradingExperiment = gradingExperiment;
        this.stages = stages;
    }

    public int getGradingTaskId() {
        return gradingTaskId;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStagesByIdx(List<Integer> stages_idxs) throws NotExistException, FormatException {
        List<Stage> baseStages = this.baseExperiment.getStages();
        List<Stage> newStages = new ArrayList<>();
        for(int i:stages_idxs){
            if(i>=baseStages.size()||i<0) throw new NotExistException("stage",""+i);
            if(baseStages.get(i).getType().equals("info")) throw new FormatException("stage with result","info stage");
            newStages.add(baseStages.get(i));
        }
        this.stages = newStages;
    }

    public String getGradingTaskName() {
        return gradingTaskName;
    }

    public List<GraderToGradingTask> getAssignedGradingTasks() {
        return assignedGradingTasks;
    }

    public void addAssignedGradingTasks(GraderToGradingTask assignedGradingTask) {
        this.assignedGradingTasks.add(assignedGradingTask);
    }

    public GraderToGradingTask graderToGradingTask(Grader g) throws NotExistException {
        // for tests use
        for(GraderToGradingTask gtgt:assignedGradingTasks){
            if(gtgt.getGrader().getGraderEmail().equals(g.getGraderEmail())) return gtgt;
        }
        throw new NotExistException("grader",g.getGraderEmail());
    }
    public Experiment getBaseExperiment() {
        return baseExperiment;
    }

    public Experiment getGeneralExperiment() {
        return generalExperiment;
    }

    public Experiment getGradingExperiment() {
        return gradingExperiment;
    }
}
