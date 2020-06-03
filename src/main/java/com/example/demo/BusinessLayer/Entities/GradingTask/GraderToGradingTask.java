package com.example.demo.BusinessLayer.Entities.GradingTask;

import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
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


    public void addGraderToParticipant(GraderToParticipant g) throws ExistException {
        for (GraderToParticipant graderToParticipant : this.graderToParticipants) {
            if(graderToParticipant.getExpeeParticipant().getParticipantId()==g.getExpeeParticipant().getParticipantId())
                throw new ExistException("user with id " + g.getExpeeParticipant().getParticipantId(), this.grader.getGraderEmail() + " participants");
        }
        this.graderToParticipants.add(g);
    }

    public void addParticipant(Participant p) throws ExistException {
        //TODO: check with sean
        GraderToParticipant g = new GraderToParticipant(this, p);
//        addGraderToParticipant(g);
    }

    public void submitResults(int pid) throws NotExistException {
        for (GraderToParticipant graderToParticipant : graderToParticipants) {
            if (graderToParticipant.getExpeeParticipant().getParticipantId() == pid) {
                graderToParticipant.setGradingState(true);
                return;
            }
        }
        throw new NotExistException("participant", "" + pid);
    }

    public boolean isSubmitted(int pid) throws NotExistException {
        for (GraderToParticipant graderToParticipant : graderToParticipants) {
            if (graderToParticipant.getExpeeParticipant().getParticipantId() == pid) {
                return graderToParticipant.getGradingState();
            }
        }
        throw new NotExistException("participant", "" + pid);
    }

    //setters
    public void setGeneralExpParticipant(Participant generalExpParticipant) {
        this.generalExpParticipant = generalExpParticipant;
    }

    public void setGraderAccessCode(UUID graderAccessCode) {
        this.graderAccessCode = graderAccessCode;
    }

    public void setGraderToParticipants(List<GraderToParticipant> graderToParticipants) {
        this.graderToParticipants = graderToParticipants;
    }

    //getters
    public List<Result> getExpeeRes(int pid) throws NotExistException, FormatException, NotInReachException {
        Participant p = getExperimenteeParticipant(pid);
        if (!p.isDone()) throw new NotInReachException(pid + " results", pid + " didn't finish the experiment");
        List<Result> ret = new ArrayList<>();
        for (Stage visible : gradingTask.getStages()) {
            ret.add(p.getResultsOf(visible));
        }
        return ret;
    }

    public List<Participant> getParticipants() {
        List<Participant> participants = new ArrayList<>();
        for (GraderToParticipant graderToparticipant : graderToParticipants) {
            participants.add(graderToparticipant.getExpeeParticipant());
        }
        return participants;
    }

    public Participant getExperimenteeParticipant(int pid) throws NotExistException {
        for (GraderToParticipant graderToParticipant : graderToParticipants) {
            if (graderToParticipant.getExpeeParticipant().getParticipantId() == pid)
                return graderToParticipant.getExpeeParticipant();
        }
        throw new NotExistException("participant", "" + pid);
    }

    public Participant getGraderParticipant(int pid) throws NotExistException {
        for (GraderToParticipant graderToParticipant : graderToParticipants) {
            if (graderToParticipant.getExpeeParticipant().getParticipantId() == pid)
//                if(graderToParticipant.getGradingState()) {
//                    //TODO:?
//                }
                return graderToParticipant.getGraderParticipant();
        }
        throw new NotExistException("participant", "" + pid);
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

    public Participant getGeneralExpParticipant() {
        return generalExpParticipant;
    }

    public GraderToGradingTaskID getGraderToGradingTaskID() {
        return graderToGradingTaskID;
    }

    public List<GraderToParticipant> getGraderToParticipants() {
        return this.graderToParticipants;
    }


}