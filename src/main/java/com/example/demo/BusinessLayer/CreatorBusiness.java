package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreatorBusiness implements ICreatorBusiness {
    @Autowired
    private DBAccess db;
    @Autowired
    private DataCache cache;

//    public CreatorBusiness() {
//        this.cache = DataCache.getInstance();
//    }

    @Override
    public boolean researcherLogin(String username, String password) {
        ManagementUser c;
        try {
            c = cache.getManagerByName(username);
        } catch (NotExistException ignore) {
            return false;
        }
        return c.getBguPassword().equals(password);
    }

    @Override
    public int createExperiment(String researcherName, String expName) throws NotExistException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        } catch (NotExistException ignore) {}

        Experiment exp = new Experiment(expName, c);
        db.saveExperiment(exp, c);
        return exp.getExperimentId();
    }

    @Override
    public void addStageToExperiment(String researcherName, int id, JSONObject stage) throws FormatException, NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(id);
        Stage toAdd = Stage.parseStage(stage, exp);
        db.saveStage(toAdd);
    }

//    @Override
//    public String saveExperiment(String researcherName, int id) throws NotExistException {
//        ManagementUser c = cache.getManagerByName(researcherName);
//
//        return "TODO: CreatorBusiness.saveExperiment";
//        //TODO:implements??
//    }

    @Override
    public int addExperiment(String researcherName, String expName, List<JSONObject> stages) throws NotExistException, FormatException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        }
        catch (NotExistException ignore) { }
        Experiment exp = buildExperiment(stages, expName, c);
        return exp.getExperimentId();
    }

    @Override
    public int addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp, List<Integer> stagesToCheck, List<JSONObject> personalExp) throws NotExistException, FormatException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        Experiment personal = buildExperiment(personalExp, gradTaskName+"/personal", c);
        Experiment forExpee = buildExperiment(ExpeeExp, gradTaskName+"/forExpee", c);
        GradingTask gt  = new GradingTask(gradTaskName, exp, personal, forExpee);
        gt.setStagesByIdx(stagesToCheck);
        cache.addGradingTask(gt);
        return gt.getGradingTaskId();
    }

    @Override
    public void addToPersonal(String researcherName, int expId, int taskId, JSONObject stage) throws NotExistException, FormatException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Experiment personal = gt.getGeneralExperiment();
        Stage toAdd = Stage.parseStage(stage, personal);
        db.saveStage(toAdd);
    }

    @Override
    public void addToResultsExp(String researcherName, int expId, int taskId, JSONObject stage) throws NotExistException, FormatException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Experiment resExp = gt.getGradingExperiment();
        Stage toAdd = Stage.parseStage(stage, resExp);
        db.saveStage(toAdd);
    }

    @Override
    public void setStagesToCheck(String researcherName, int expId, int taskId, List<Integer> stagesToCheck) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        gt.setStagesByIdx(stagesToCheck);
        db.saveGradingTask(gt);
    }

//    @Override
//    public String saveGradingTask(String researcherName, int expId, int taskId) throws NotExistException {
//        ManagementUser c = cache.getManagerByName(researcherName);
//        if (c == null) return researcherName + " not exist";
//        return "TODO:CreatorBusiness.saveGradingTask";
//        //TODO:implements??
//    }

    @Override
    public void setAlliePermissions(String researcherName, int expId, String allieMail, String allieRole, List<String> permissions) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);

        ManagementUser ally;
        try{
            ally = cache.getManagerByEMail(allieMail);
        }catch (NotExistException ignore){
            throw new NotExistException("set allie permissions", "allie not found");
        }
        ManagementUserToExperiment m = new ManagementUserToExperiment(ally, exp, allieRole);
        db.saveManagementUserToExperiment(m);
        db.deletePermissionsOfManagementUser(ally);
        for (String per : permissions) {
            Permission toAdd = new Permission(per, ally);
            db.savePermissionForManagementUser(toAdd, ally);
        }
    }

    @Override
    public void addGraderToGradingTask(String researcherName, int expId, int taskId, String graderMail) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Grader grader;
        try {
            grader = cache.getGraderByEMail(graderMail);
        }catch (NotExistException ignore){
            grader = new Grader(graderMail, gt.getBaseExperiment());
            cache.addGrader(grader);
        }
        cache.addGraderToGradingTask(gt, grader);//TODO:figure out WTF
    }

    @Override
    public String addExperimentee(String researcherName, int expId, String expeeMail) throws NotExistException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        if (cache.isExpeeInExperiment(expeeMail, expId)) throw new ExistException(expeeMail, "experiment " + expId);
        Experimentee expee =new Experimentee(expeeMail, exp);
        cache.addExperimentee(expee);
        return expee.getAccessCode().toString();
    }

    @Override
    public void addExpeeToGrader(String researcherName, int expId, int taskId, String graderMail, String expeeMail) throws NotExistException, ExistException {
        Grader grader = cache.getGraderByEMail(graderMail);
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Participant participant = cache.getExpeeByMailAndExp(expeeMail, expId).getParticipant();
        GradersGTToParticipants g = cache.getGradersGTToParticipants(cache.getGraderToGradingTask(grader, gt), participant);
        if(g != null) { //this participant is already in the graderToGraderTask
            throw new ExistException("user with id "+participant.getParticipantId(),graderMail+" participants");
        }
        else {
            g = new GradersGTToParticipants(cache.getGraderToGradingTask(grader, gt), participant);
        }
        cache.addExpeeToGradingTask(gt, grader, g);
        //TODO: fix?
    }

    //meaningful getters

    public List<Experiment> getExperiments(String username) throws NotExistException {
        ManagementUser manager = cache.getManagerByName(username);
        return manager.getExperimentes();
    }

    public List<Stage> getStages(String username,int expId) throws NotExistException {
        ManagementUser c = cache.getManagerByName(username);
        Experiment exp = c.getExperiment(expId);
        return exp.getStages();
    }

    public List<Participant> getExperimentees(String username, int expId) throws NotExistException {
        ManagementUser c = cache.getManagerByName(username);
        Experiment exp = c.getExperiment(expId);
        return exp.getParticipants();
    }

    public List<ManagementUserToExperiment> getAllies(String username, int expId) throws NotExistException {
        ManagementUser c = cache.getManagerByName(username);
        Experiment exp = c.getExperiment(expId);
        return exp.getManagementUserToExperiments();
    }

    public List<GradingTask> getGradingTasks(String username, int expId) throws NotExistException{
        return cache.getAllGradingTasks(username,expId);
    }

    @Override
    public List<Stage> getPersonalStages(String username, int expId, int taskId) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(username, expId, taskId);
        return gt.getGeneralExperiment().getStages();
    }

    @Override
    public List<Stage> getEvaluationStages(String username, int expId, int taskId) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(username, expId, taskId);
        return gt.getGradingExperiment().getStages();
    }

    @Override
    public List<Grader> getTaskGraders(String username, int expId, int taskId) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(username, expId, taskId);
        List<GraderToGradingTask> assignedGradingTasks = gt.getAssignedGradingTasks();
        List<Grader> graders = new ArrayList<>();
        for(GraderToGradingTask graderToTask:assignedGradingTasks){
            graders.add(graderToTask.getGrader());
        }
        return graders;
    }

    @Override
    public List<Participant> getTaskExperimentees(String username, int expId, int taskId) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(username, expId, taskId);
        List<GraderToGradingTask> assignedGradingTasks = gt.getAssignedGradingTasks();
        List<Participant> experimentees = new ArrayList<>();
        for(GraderToGradingTask graderToTask:assignedGradingTasks){
            experimentees.addAll(graderToTask.getParticipants());
        }
        return experimentees;
    }

    // utils
    private Experiment buildExperiment(List<JSONObject> stages, String expName, ManagementUser creator) throws FormatException {
        Experiment exp = new Experiment(expName, creator);
        db.saveExperiment(exp, creator);
        for (JSONObject jStage : stages) {
            try{
                Stage toAdd = Stage.parseStage(jStage, exp);
                db.saveStage(toAdd);
            }catch (FormatException e){
                creator.removeManagementUserToExperimentById(exp);
                db.deleteExperiment(exp);
                throw e;
            }
        }
        return exp;
    }
}
