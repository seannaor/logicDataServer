package com.example.demo;

import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Stages.*;
import com.example.demo.DataAccessLayer.Reps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class DBAccess {
    @Autowired
    ExperimenteeRep experimenteeRep;
    @Autowired
    ExperimentRep experimentRep;
    @Autowired
    GraderRep graderRep;
    @Autowired
    ManagementUserRep managementUserRep;
    @Autowired
    ParticipantRep participantRep;
    @Autowired
    PermissionRep permissionRep;
    @Autowired
    StageRep stageRep;
    @Autowired
    QuestionRep questionRep;
    @Autowired
    AnswerRep answerRep;
    @Autowired
    CodeResultRep codeResultRep;
    @Autowired
    RequirementRep requirementRep;
    @Autowired
    RequirementTagRep requirementTagRep;
    @Autowired
    GradingTaskRep gradingTaskRep;
    @Autowired
    GraderToGradingTaskRep graderToGradingTaskRep;
    @Autowired
    GradersGTToParticipantsRep gradersGTToParticipantsRep;
    @Autowired
    ManagementUserToExperimentRep managementUserToExperimentRep;
    @Autowired
    DataCache cache;

    public void deleteData() {
        JpaRepository[] reps = {
                managementUserRep,
                experimenteeRep,
                graderRep,
                participantRep,
                stageRep,
                experimentRep,
                permissionRep,
                questionRep,
                answerRep,
                codeResultRep,
                requirementRep,
                requirementTagRep,
                gradingTaskRep,
                graderToGradingTaskRep,
                gradersGTToParticipantsRep,
                managementUserToExperimentRep
        };
        for (JpaRepository rep : reps)
            rep.deleteAll();
    }
    public void saveExperimentee(Experimentee e) {
        participantRep.save(e.getParticipant());
        experimenteeRep.save(e);
    }
    public List<Experimentee> getAllExperimentees() {
        return experimenteeRep.findAll();
    }
    public Experimentee getExperimenteeByCode(String code) { return experimenteeRep.findById(code).orElse(null); }
    public Experimentee getExperimenteeByEmail(String email) { return experimenteeRep.findByEmail(email); }
    public Experimentee getExperimenteeByEmailAndExp(String email, int expId) { return experimenteeRep.findByEmailAndExp(email, expId); }
    public void saveExperiment(Experiment e, ManagementUser creator) {
        experimentRep.save(e);
        for(ManagementUserToExperiment m : creator.getManagementUserToExperiments()) {
            if(m.getExperiment().getExperimentId() == e.getExperimentId()) {
                managementUserToExperimentRep.save(m);
            }
        }
        managementUserRep.save(creator);
        //cache.updateManagementUser(creator);
    }
    public void deleteExperiment(Experiment e) {
        experimentRep.deleteById(e.getExperimentId());
    }
    public Experiment getExperimentById(int expId) { return experimentRep.findById(expId).orElse(null); }
    public void saveGrader(Grader g) {
        participantRep.save(g.getParticipant());
        graderRep.save(g);
    }
    public Grader getGraderByEmail(String email) { return graderRep.findById(email).orElse(null); }
    public void saveManagementUser(ManagementUser m) {
        managementUserRep.save(m);
    }
    public void deleteManagementUser(ManagementUser m) {
        managementUserRep.delete(m);
    }
    public List<ManagementUser> getAllManagementUsers() {
        return managementUserRep.findAll();
    }
    public ManagementUser getManagementUserByName(String name) { return managementUserRep.findById(name).orElse(null); }
    public ManagementUser getManagementUserByEMail(String email) { return managementUserRep.findByEmail(email); }
    public void saveParticipant(Participant p) { participantRep.save(p); }
    public Participant getParticipantById(int pId) { return participantRep.findById(pId).orElse(null); }
    public void savePermissionForManagementUser(Permission p, ManagementUser m) { permissionRep.save(p); managementUserRep.save(m); }
    public void deletePermissionsOfManagementUser(ManagementUser m) {
        for(Permission p : m.getPermissions())
            permissionRep.delete(p);
        m.setPermissions(new ArrayList<>());
        managementUserRep.save(m);
    }
    public void saveStage(Stage s) {
        if(s instanceof QuestionnaireStage) {
            List<Question> temp = ((QuestionnaireStage) s).getQuestions();
            ((QuestionnaireStage) s).setQuestions(new ArrayList<>());
            stageRep.save(s);
            for(Question q : temp) {
                questionRep.save(q);
            }
            ((QuestionnaireStage) s).setQuestions(temp);
        }
        if(s instanceof CodeStage) {
            List<Requirement> temp = ((CodeStage) s).getRequirements();
            ((CodeStage) s).setRequirements(new ArrayList<>());
            stageRep.save(s);
            for(Requirement r : temp) {
                requirementRep.save(r);
            }
            ((CodeStage) s).setRequirements(temp);
        }
        stageRep.save(s);
        experimentRep.save(s.getExperiment());
    }
    public void saveQuestion(Question q) { questionRep.save(q); }
    public void saveAnswer(Answer a) { answerRep.save(a); }
    public void saveCodeResult(CodeResult cr) { codeResultRep.save(cr); }
    public void saveRequirement(Requirement r) { requirementRep.save(r); }
    public void saveRequirementTag(RequirementTag rt) { requirementTagRep.save(rt); }
    public void saveGradingTask(GradingTask gt) { gradingTaskRep.save(gt); }
    public GradingTask getGradingTaskById(int gtId) { return gradingTaskRep.findById(gtId).orElse(null); }
    public List<GradingTask> getAllGradingTasks() { return gradingTaskRep.findAll(); }
    public void saveGraderToGradingTask(GraderToGradingTask g) {
        graderToGradingTaskRep.save(g);
        graderRep.save(g.getGrader());
        gradingTaskRep.save(g.getGradingTask());
//        cache.updateGrader(g.getGrader());
//        cache.updateGradingTask(g.getGradingTask());
    }
    public long getGraderToGradingTaskCount() { return graderToGradingTaskRep.count(); }
    public GraderToGradingTask getGraderToGradingTaskById(int gtId, String graderEmail) { return graderToGradingTaskRep.findById(new GraderToGradingTask.GraderToGradingTaskID(gtId, graderEmail)).orElse(null); }
    public GraderToGradingTask getGraderToGradingTaskByCode(String code) { return graderToGradingTaskRep.findByGradersCode(code); }
    public void saveGradersGTToParticipants(GradersGTToParticipants g) {
        gradersGTToParticipantsRep.save(g);
        graderToGradingTaskRep.save(g.getGraderToGradingTask());
        participantRep.save(g.getParticipant());
    }
    public GradersGTToParticipants getGradersGTToParticipantsById(int gtId, String graderEmail, int pId) { return gradersGTToParticipantsRep.findById(new GradersGTToParticipants.GradersGTToParticipantsID(gtId, graderEmail, pId)).orElse(null); }
    public void saveManagementUserToExperiment(ManagementUserToExperiment m) { managementUserToExperimentRep.save(m); }
}