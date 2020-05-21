package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "graders_to_grading_tasks")
public class GraderToGradingTask {
    @Embeddable
    public static class GraderToGradingTaskID implements Serializable {
        @Column(name = "grading_task_id")
        private int gradingTaskId;
        @Column(name = "grader_email")
        private String graderEmail;

        public GraderToGradingTaskID() {
        }

        public GraderToGradingTaskID(int gradingTaskId, String graderEmail) {
            this.gradingTaskId = gradingTaskId;
            this.graderEmail = graderEmail;
        }

        public int getGradingTaskId() {
            return gradingTaskId;
        }

        public String getGraderEmail() {
            return graderEmail;
        }
    }

    @EmbeddedId
    private GraderToGradingTaskID graderToGradingTaskID;
    @MapsId("gradingTaskId")
    @ManyToOne
    @JoinColumn(name = "grading_task_id")
    private GradingTask gradingTask;
    @MapsId("graderEmail")
    @ManyToOne
    @JoinColumn(name = "grader_email")
    private Grader grader;
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "grader_access_code", columnDefinition = "BINARY(16)")
    private UUID graderAccessCode;
    @OneToOne
    @JoinColumn(name = "general_exp_participant", referencedColumnName = "participant_id")
    private Participant generalExpParticipant;
    @OneToMany(mappedBy = "graderToGradingTask")
    private List<GraderToParticipant> graderToParticipants = new ArrayList<>();

    public GraderToGradingTask() {
    }

    public GraderToGradingTask(GradingTask gradingTask, Grader grader) {
        this.graderToGradingTaskID = new GraderToGradingTaskID(gradingTask.getGradingTaskId(), grader.getGraderEmail());
        this.gradingTask = gradingTask;
        this.grader = grader;
        this.generalExpParticipant = new Participant(this.gradingTask.getGeneralExperiment());
        this.gradingTask.addAssignedGradingTasks(this);
        this.grader.assignGradingTasks(this);
    }

    public Participant getGeneralExpParticipant() {
        return generalExpParticipant;
    }

    public void setGeneralExpParticipant(Participant generalExpParticipant) {
        this.generalExpParticipant = generalExpParticipant;
    }

    public GraderToGradingTaskID getGraderToGradingTaskID() {
        return graderToGradingTaskID;
    }

    public List<GraderToParticipant> getGraderToParticipants() {
        return this.graderToParticipants;
    }

    public void setGraderToParticipants(List<GraderToParticipant> graderToParticipants) {
        this.graderToParticipants = graderToParticipants;
    }

    public void addGraderToParticipant(GraderToParticipant g) throws ExistException {
        if (this.graderToParticipants.contains(g))
            throw new ExistException("user with id " + g.getExpeeParticipant().getParticipantId(), this.grader.getGraderEmail() + " participants");
        this.graderToParticipants.add(g);
    }

    public List<Participant> getParticipants() {
        List<Participant> participants = new ArrayList<>();
        for(GraderToParticipant graderToparticipant: graderToParticipants){
            participants.add(graderToparticipant.getExpeeParticipant());
        }
        return participants;
    }

    public void addParticipant(Participant p) throws ExistException {
        //TODO: check with sean
        GraderToParticipant graderToParticipant = new GraderToParticipant(this,p);
        if (graderToParticipants.contains(graderToParticipant))
            throw new ExistException("user with id " + p.getParticipantId(), this.grader.getGraderEmail() + " participants");
        graderToParticipants.add(graderToParticipant);
    }

    public GradingTask getGradingTask() {
        return gradingTask;
    }

    public Grader getGrader() {
        return grader;
    }

    public UUID getGraderAccessCode() {
        return graderAccessCode;
    }

    public void setGraderAccessCode(UUID graderAccessCode) {
        this.graderAccessCode = graderAccessCode;
    }

    public List<Result> getExpeeRes(int parti_id) throws NotExistException, FormatException {
        Participant p = getParti(parti_id);
        List<Result> ret = new ArrayList<>();
        for (Stage visible : gradingTask.getStages()) {
            ret.add(p.getResultsOf(visible));
        }
        return ret;
    }

    private Participant getParti(int parti_id) throws NotExistException {
        for (GraderToParticipant graderToParticipant : graderToParticipants) {
            if (graderToParticipant.getExpeeParticipant().getParticipantId() == parti_id)
                return graderToParticipant.getExpeeParticipant();
        }
        throw new NotExistException("participant", "" + parti_id);
    }
}