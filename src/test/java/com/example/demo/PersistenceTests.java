package com.example.demo;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Stages.*;
import com.example.demo.DataAccessLayer.Reps.*;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


@Sql({"/create_database.sql"})
@SpringBootTest
class PersistenceTests {

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
	InfoStageRep infoStageRep;
	@Autowired
	QuestionnaireStageRep questionnaireStageRep;
	@Autowired
	CodeStageRep codeStageRep;
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

	@BeforeEach
	void clean() {
		JpaRepository[] reps = {
				managementUserRep,
				experimenteeRep,
				graderRep,
				participantRep,
				stageRep,
				experimentRep,
				permissionRep,
				infoStageRep,
				questionnaireStageRep,
				codeStageRep,
				questionRep,
				answerRep,
				codeResultRep,
				requirementRep,
				requirementTagRep,
				gradingTaskRep,
				graderToGradingTaskRep
		};
		for (JpaRepository rep : reps)
			rep.deleteAll();
	}

	@Test
	@Transactional
	void managementUserCRUDTest() {
		addManagementUsers();
		assertEquals(managementUserRep.count(), 2);
		addPermissionsToManagementUsers();
		assertEquals(permissionRep.count(), 2);
		assertEquals(managementUserRep.findAll().get(0).getPermissions().size(), 1);
		assertEquals(managementUserRep.findAll().get(1).getPermissions().size(), 1);
		assertEquals(permissionRep.findAll().get(0).getManagementUsers().size(), 1);
		assertEquals(permissionRep.findAll().get(1).getManagementUsers().size(), 1);
		updateManagementUsers();
		assertEquals(managementUserRep.findById("User1").orElse(null).getUserEmail(), "user1@gmail.com");
		assertEquals(managementUserRep.findById("User2").orElse(null).getUserEmail(), "user2@gmail.com");
		deleteManagementUsers();
		assertEquals(managementUserRep.count(), 1);
		assertEquals(permissionRep.count(), 2);
	}

	private void addManagementUsers() {
		ManagementUser u1 = new ManagementUser("User1", "Pass1", "u1@gmail.com"),
				u2 = new ManagementUser("User2", "Password2", "User2@yahoo.com");
		managementUserRep.save(u1);
		managementUserRep.save(u2);
	}

	private void addPermissionsToManagementUsers() {
		Permission p1 = new Permission("Super Manager"),
				p2 = new Permission("Noob");
		p1.getManagementUsers().add(managementUserRep.findAll().get(0));
		p2.getManagementUsers().add(managementUserRep.findAll().get(1));
		managementUserRep.findAll().get(0).getPermissions().add(p1);
		managementUserRep.findAll().get(1).getPermissions().add(p2);
		permissionRep.save(p1);
		permissionRep.save(p2);
	}

	private void updateManagementUsers() {
		ManagementUser managementUser1 = managementUserRep.findById("User1").orElse(null);
		managementUser1.setUserEmail("user1@gmail.com");
		ManagementUser managementUser2 = managementUserRep.findById("User2").orElse(null);
		managementUser2.setUserEmail("user2@gmail.com");
		managementUserRep.save(managementUser1);
		managementUserRep.save(managementUser2);
	}

	private void deleteManagementUsers() {
		ManagementUser managementUser = managementUserRep.findById("User2").orElse(null);
		managementUserRep.delete(managementUser);
	}

	@Test
	@Transactional
	void experimenteeCRUDTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		Experimentee expee1 = new Experimentee("123asd", "a@a.com"),
				expee2 = new Experimentee("vxcf", "b@a.com"),
				expee3 = new Experimentee("asdiiji", "c@a.com");
		Participant p1 = new Participant(e),
				p2 = new Participant(e),
				p3 = new Participant(e);
		expee1.setParticipant(p1);
		expee2.setParticipant(p2);
		expee3.setParticipant(p3);
		participantRep.save(p1);
		participantRep.save(p2);
		participantRep.save(p3);
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		experimenteeRep.save(expee3);
		assertEquals(experimenteeRep.count(), 3);
		expee1.setExperimenteeEmail("aa@aa.com");
		expee2.setExperimenteeEmail("bb@bb.com");
		expee3.setExperimenteeEmail("cc@cc.com");
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		experimenteeRep.save(expee3);
		assertEquals(experimenteeRep.findById("123asd").orElse(null).getExperimenteeEmail(), "aa@aa.com");
		assertEquals(experimenteeRep.findById("vxcf").orElse(null).getExperimenteeEmail(), "bb@bb.com");
		assertEquals(experimenteeRep.findById("asdiiji").orElse(null).getExperimenteeEmail(), "cc@cc.com");
		experimenteeRep.deleteAll();
		assertEquals(experimenteeRep.count(), 0);
	}

//	@Test
//	@Transactional
//	void gradersAndGradingTasksCRUDTest() {
//		Experiment e = new Experiment("hi");
//		experimentRep.save(e);
//		Experimentee expee1 = new Experimentee("123asd", "a@a.com");
//		Participant pOfExpee1 = new Participant(e);
//		expee1.setParticipant(pOfExpee1);
//		participantRep.save(pOfExpee1);
//		experimenteeRep.save(expee1);
//		Experimentee expee2 = new Experimentee("aaa", "b@b.com");
//		Participant pOfExpee2 = new Participant(e);
//		expee2.setParticipant(pOfExpee2);
//		participantRep.save(pOfExpee2);
//		experimenteeRep.save(expee2);
//		Participant pOfGrader1 = new Participant(e);
//		participantRep.save(pOfGrader1);
//		Grader g1 = new Grader("mosh@gmail.com", pOfGrader1);
//		graderRep.save(g1);
//		Participant pOfGrader2 = new Participant(e);
//		participantRep.save(pOfGrader2);
//		Grader g2 = new Grader("shalom@gmail.com", pOfGrader2);
//		graderRep.save(g2);
//		assertEquals(graderRep.count(), 2);
//		assertEquals(graderRep.findById(g1.getGraderEmail()).orElse(null).getGraderEmail(), "mosh@gmail.com");
//		e.addParticipant(pOfExpee1);
//		e.addParticipant(pOfExpee2);
//		e.addParticipant(pOfGrader1);
//		e.addParticipant(pOfGrader2);
//		InfoStage infoStage1 = new InfoStage("hi", e);
//		stageRep.save(infoStage1);
//		e.addStage(infoStage1);
//		experimentRep.save(e);
//		InfoStage infoStage2 = new InfoStage("bye", e);
//		stageRep.save(infoStage2);
//		e.addStage(infoStage2);
//		experimentRep.save(e);
//		assertEquals(stageRep.count(), 2);
//		Experiment baseExp = new Experiment("baseExp");
//		experimentRep.save(baseExp);
//		Set<Stage> stagesForGT1 = new HashSet<>();
//		stagesForGT1.add(infoStage1);
// 		GradingTask gt1 = new GradingTask(e, e, e, stagesForGT1);
//		gradingTaskRep.save(gt1);
//		assertEquals(gradingTaskRep.count(), 1);
//		Set<Stage> stagesForGT2 = new HashSet<>();
//		stagesForGT2.add(infoStage2);
//		GradingTask gt2 = new GradingTask(e, e, e, stagesForGT2);
//		gradingTaskRep.save(gt2);
//		assertEquals(gradingTaskRep.count(), 2);
//		assertEquals(gradingTaskRep.findById(gt1.getGradingTaskId()).orElse(null).getStages().size(), 1);
//		Set<Participant> participantsFor1 = new HashSet<>();
//		Set<Participant> participantsFor2 = new HashSet<>();
//		participantsFor1.add(pOfExpee1);
//		participantsFor2.add(pOfExpee2);
//		GraderToGradingTask graderToGradingTask1 = new GraderToGradingTask(gt1, g1, "1", participantsFor1);
//		GraderToGradingTask graderToGradingTask2 = new GraderToGradingTask(gt2, g2, "2", participantsFor2);
//		graderToGradingTaskRep.save(graderToGradingTask1);
//		assertEquals(graderToGradingTaskRep.count(), 1);
//		graderToGradingTaskRep.save(graderToGradingTask2);
//		assertEquals(graderToGradingTaskRep.count(), 2);
//		g1.assignGradingTasks(graderToGradingTask1);
//		g2.assignGradingTasks(graderToGradingTask2);
//		graderRep.save(g1);
//		graderRep.save(g2);
//		assertEquals(graderRep.findById(g1.getGraderEmail()).orElse(null).getAssignedGradingTasks().size(), 1);
//		assertEquals(graderRep.findById(g2.getGraderEmail()).orElse(null).getAssignedGradingTasks().size(), 1);
//		gt1.addAssignedGradingTasks(graderToGradingTask1);
//		gt2.addAssignedGradingTasks(graderToGradingTask2);
//		gradingTaskRep.save(gt1);
//		gradingTaskRep.save(gt2);
//		assertEquals(gradingTaskRep.findById(gt1.getGradingTaskId()).orElse(null).getAssignedGradingTasks().size(), 1);
//		assertEquals(gradingTaskRep.findById(gt2.getGradingTaskId()).orElse(null).getAssignedGradingTasks().size(), 1);
//	}

	@Test
	void stagesCRUDTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		InfoStage s = new InfoStage("hello", e);
		QuestionnaireStage s1 = new QuestionnaireStage(e);
		stageRep.save(s);
		stageRep.save(s1);
		assertEquals(stageRep.count(), 2);
		assertEquals(infoStageRep.findAll().get(0).getInfo(), "hello");
		s.setInfo("hi there");
		stageRep.save(s);
		assertEquals(stageRep.count(), 2);
		assertEquals(infoStageRep.findAll().get(0).getInfo(), "hi there");
		CodeStage s2 = new CodeStage("hello", "", e);
		TaggingStage s3 = new TaggingStage(s2, e);
		stageRep.save(s2);
		stageRep.save(s3);
		assertEquals(stageRep.count(), 4);
		assertEquals(codeStageRep.findAll().get(0).getTemplate(), "");
		s2.setTemplate("int main() {");
		stageRep.save(s2);
		assertEquals(stageRep.count(), 4);
		assertEquals(codeStageRep.findAll().get(0).getTemplate(), "int main() {");
		stageRep.deleteAll();
		assertEquals(stageRep.count(), 0);
		assertEquals(infoStageRep.count(), 0);
		assertEquals(codeStageRep.count(), 0);
	}

	@Test
	@Transactional
	void questionsAndAnswersCRUDTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		JSONObject jsonQuestion1 = new JSONObject();
		jsonQuestion1.put("how old are you?", new String[] {"10-20", "20-30", "30-40", "40+"});
		JSONObject jsonQuestion2 = new JSONObject();
		jsonQuestion2.put("favorite programming language?", new String[] {"c", "c++", "java", "python"});
		List<JSONObject> questions = new ArrayList<>();
		questions.add(jsonQuestion1);
		questions.add(jsonQuestion2);
		QuestionnaireStage s = new QuestionnaireStage(e);
		stageRep.save(s);
		assertEquals(questionRep.count(), 0);
		assertEquals(stageRep.count(), 1);
		assertEquals(questionnaireStageRep.count(), 1);
		int QIdx = 1;
		for (JSONObject JQuestion : questions) {
			Question q = new Question(QIdx, s, JQuestion.toJSONString());
			questionRep.save(q);
			s.addQuestion(q);
			QIdx++;
		}
		stageRep.save(s);
		assertEquals(questionRep.count(), 2);
		assertEquals(questionnaireStageRep.findAll().get(0).getQuestions().size(), 2);
		JSONObject updatedJsonQuestion = new JSONObject();
		updatedJsonQuestion.put("how old are you?", new String[] {"10-20", "21-30", "31-40", "41+"});
		Question q = (Question) s.getQuestions().toArray()[0];
		q.setQuestionJson(updatedJsonQuestion.toJSONString());
		//TODO: check this, trying to update a question but first we have to update the stage, not very nice
		s.addQuestion(q);
		stageRep.save(s);
		questionRep.save(q);
		assertEquals(questionRep.count(), 2);
		assertEquals(questionRep.findById(q.getQuestionID()).orElse(null).getQuestionJson(), updatedJsonQuestion.toJSONString());
		Experimentee expee1 = new Experimentee("123asd", "a@a.com");
		Participant p1 = new Participant(experimentRep.findAll().get(0));
		experimentRep.findAll().get(0).getParticipants().add(p1);
		expee1.setParticipant(p1);
		participantRep.save(p1);
		experimenteeRep.save(expee1);
		Answer answer1 = new Answer(2, (Question)s.getQuestions().toArray()[0], p1);
		answerRep.save(answer1);
		Answer answer2 = new Answer(3, (Question)s.getQuestions().toArray()[1], p1);
		answerRep.save(answer2);
		assertEquals(answerRep.count(), 2);
		List<Question> updated = new ArrayList<>(s.getQuestions());
		Question toRemove = (Question)s.getQuestions().toArray()[0];
		updated.remove(toRemove);
		s.setQuestions(updated);
		stageRep.save(s);
		answerRep.delete(answer1);
		assertEquals(answerRep.count(), 1);
		questionRep.deleteById(toRemove.getQuestionID());
		assertEquals(questionRep.count(), 1);
		answerRep.delete(answer2);
		assertEquals(answerRep.count(), 0);
		Question toRemove1 = (Question)s.getQuestions().toArray()[0];
		s.setQuestions(new ArrayList<>());
		stageRep.save(s);
		questionRep.delete(toRemove1);
		assertEquals(questionRep.count(), 0);
		assertEquals(questionnaireStageRep.findAll().get(0).getQuestions().size(), 0);
	}

	@Test
	@Transactional
	void codeStageAndTaggingCRUDTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		assertEquals(experimentRep.count(), 1);
		CodeStage codeStage = new CodeStage("welcome", "", e);
		stageRep.save(codeStage);
		assertEquals(stageRep.count(), 1);
		assertEquals(codeStageRep.findAll().get(0).getRequirements().size(), 0);
		List<Requirement> requirements = new ArrayList<>();
		Requirement r1 = new Requirement(codeStage, "write a function that prints Hello World!");
		requirements.add(r1);
		requirementRep.save(r1);
		codeStage.setRequirements(requirements);
		stageRep.save(codeStage);
		assertEquals(requirementRep.findAll().get(0).getText(), "write a function that prints Hello World!");
		r1.setText("please blabla write a function that prints Hello World!");
		requirementRep.save(r1);
		stageRep.save(codeStage);
		assertEquals(requirementRep.findAll().get(0).getText(), "please blabla write a function that prints Hello World!");
		assertEquals(((Requirement)codeStageRep.findAll().get(0).getRequirements().toArray()[0]).getText(), "please blabla write a function that prints Hello World!");
		Requirement r2 = new Requirement(codeStage, "function must have 1 line");
		requirements.add(r2);
		requirementRep.save(r2);
		assertEquals(requirementRep.count(), 2);
		codeStage.setRequirements(requirements);
		stageRep.save(codeStage);
		assertEquals(codeStageRep.findAll().get(0).getRequirements().size(), 2);
		Experimentee expee1 = new Experimentee("123asd", "a@a.com");
		Participant p1 = new Participant(experimentRep.findAll().get(0));
		experimentRep.findAll().get(0).getParticipants().add(p1);
		expee1.setParticipant(p1);
		participantRep.save(p1);
		experimenteeRep.save(expee1);
		assertEquals(participantRep.count(), 1);
		assertEquals(experimenteeRep.count(), 1);
		CodeResult codeResult = new CodeResult(p1, codeStage, "System.out.println(\"hello world\");");
		codeResultRep.save(codeResult);
		assertEquals(codeResultRep.count(), 1);
		assertEquals(codeResultRep.findAll().get(0).getUserCode(), "System.out.println(\"hello world\");");
		codeResult.setUserCode("System.out.println(\"Hello World\");");
		codeResultRep.save(codeResult);
		assertEquals(codeResultRep.count(), 1);
		assertEquals(codeResultRep.findAll().get(0).getUserCode(), "System.out.println(\"Hello World\");");
		TaggingStage taggingStage = new TaggingStage(codeStage, e);
		stageRep.save(taggingStage);
		assertEquals(stageRep.count(), 2);
		List<Requirement> requirementsFor_rt1 = new ArrayList<>();
		requirementsFor_rt1.add(r1);
		requirementsFor_rt1.add(r2);
		RequirementTag rt1 = new RequirementTag(0, 10, p1, requirementsFor_rt1);
		List<Requirement> requirementsFor_rt2 = new ArrayList<>();
		requirementsFor_rt2.add(r1);
		RequirementTag rt2 = new RequirementTag(10, 15, p1, requirementsFor_rt2);
		requirementTagRep.save(rt1);
		requirementTagRep.save(rt2);
		assertEquals(requirementTagRep.count(), 2);
		assertEquals(requirementTagRep.findAll().get(0).getLength(), 10);
		rt1.setLength(13);
		requirementTagRep.save(rt1);
		assertEquals(requirementTagRep.count(), 2);
		assertEquals(requirementTagRep.findAll().get(0).getLength(), 13);
		requirementTagRep.deleteById(rt1.getRequirementTagID());
		assertEquals(requirementTagRep.count(), 1);
		List<Requirement> updated = new ArrayList<>();
		updated.add(r1);
		codeStage.setRequirements(updated);
		requirementRep.deleteById(r2.getRequirementID());
		assertEquals(requirementRep.count(), 1);
		stageRep.save(codeStage);
		assertEquals(codeStageRep.findAll().get(0).getRequirements().size(), 1);
		codeResultRep.deleteById(codeResult.getCodeResultID());
		assertEquals(codeResultRep.count(), 0);
	}

	@Test
	@Transactional
	void createExperimentsTest() {
		addExperiments();
		assertEquals(experimentRep.count(), 2);
		addManagementUsersToExperiments();
		assertEquals(managementUserRep.findAll().get(0).getExperiments().size(), 1);
		assertEquals(managementUserRep.findAll().get(1).getExperiments().size(), 1);
		assertEquals(experimentRep.findAll().get(0).getManagementUsers().size(), 1);
		assertEquals(experimentRep.findAll().get(1).getManagementUsers().size(), 1);
		addExperimenteesToExperiments();
		assertEquals(experimenteeRep.count(), 3);
		assertEquals(participantRep.count(), 3);
		assertEquals(experimentRep.findAll().get(0).getParticipants().size(), 2);
		assertEquals(experimentRep.findAll().get(1).getParticipants().size(), 1);
		assignStagesToExperiments();
		assertEquals(stageRep.count(), 10);
		assertEquals(experimentRep.findAll().get(0).getStages().size(), 5);
		assertEquals(experimentRep.findAll().get(1).getStages().size(), 5);
	}

	private void addExperiments() {
		Experiment e1 = new Experiment("Exp1"),
				e2 = new Experiment("Expr2");
		experimentRep.save(e1);
		experimentRep.save(e2);
	}

	private void addManagementUsersToExperiments() {
		ManagementUser u1 = new ManagementUser("User1", "123Pass", "u1@u1mail.com");
		ManagementUser u2 = new ManagementUser("User2", "123Pass", "u2@u2mail.com");
		u1.getExperiments().add(experimentRep.findAll().get(0));
		u2.getExperiments().add(experimentRep.findAll().get(1));
		experimentRep.findAll().get(0).getManagementUsers().add(u1);
		experimentRep.findAll().get(1).getManagementUsers().add(u2);
		managementUserRep.save(u1);
		managementUserRep.save(u2);
	}

	private void addExperimenteesToExperiments() {
		Experimentee expee1 = new Experimentee("123asd", "a@a.com"),
				expee2 = new Experimentee("vxcf", "b@a.com"),
				expee3 = new Experimentee("asdiiji", "c@a.com");
		Participant p1 = new Participant(experimentRep.findAll().get(0)),
				p2 = new Participant(experimentRep.findAll().get(0)),
				p3 = new Participant(experimentRep.findAll().get(1));
		expee1.setParticipant(p1);
		expee2.setParticipant(p2);
		expee3.setParticipant(p3);
		participantRep.save(p1);
		participantRep.save(p2);
		participantRep.save(p3);
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		experimenteeRep.save(expee3);
	}

	private void assignStagesToExperiments() {
		InfoStage s11 = new InfoStage("hello", experimentRep.findAll().get(0));
		QuestionnaireStage s21 = new QuestionnaireStage(experimentRep.findAll().get(0));
		InfoStage s31 = new InfoStage("good luck", experimentRep.findAll().get(0));
		CodeStage s41 = new CodeStage("enter your code", "", experimentRep.findAll().get(0));
		TaggingStage s51 = new TaggingStage(s41, experimentRep.findAll().get(0));
		InfoStage s12 = new InfoStage("hello", experimentRep.findAll().get(1));
		QuestionnaireStage s22 = new QuestionnaireStage(experimentRep.findAll().get(1));
		InfoStage s32 = new InfoStage("good luck", experimentRep.findAll().get(1));
		CodeStage s42 = new CodeStage("enter your code", "", experimentRep.findAll().get(1));
		TaggingStage s52 = new TaggingStage(s41, experimentRep.findAll().get(1));
		stageRep.save(s11);
		stageRep.save(s21);
		stageRep.save(s31);
		stageRep.save(s41);
		stageRep.save(s51);
		stageRep.save(s12);
		stageRep.save(s22);
		stageRep.save(s32);
		stageRep.save(s42);
		stageRep.save(s52);
	}
}