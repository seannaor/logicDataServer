package com.example.demo;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
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
	@Autowired
	GradersGTToParticipantsRep gradersGTToParticipantsRep;
	@Autowired
	ManagementUserToExperimentRep managementUserToExperimentRep;

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
				graderToGradingTaskRep,
				gradersGTToParticipantsRep,
				managementUserToExperimentRep
		};
		for (JpaRepository rep : reps)
			rep.deleteAll();
	}

	@Test
	@Transactional
	void addManagementUserTest() {
		addManagementUsers();
		assertEquals(managementUserRep.count(), 2);
	}

	@Test
	@Transactional
	void addManagementUserWithPermissionsTest() {
		addManagementUsers();
		addPermissionsToManagementUsers();
		assertEquals(permissionRep.count(), 2);
		assertEquals(managementUserRep.findAll().get(0).getPermissions().size(), 1);
		assertEquals(managementUserRep.findAll().get(0).getPermissions().get(0).getPermissionName(), "Super Manager");
		assertEquals(managementUserRep.findAll().get(1).getPermissions().size(), 1);
		assertEquals(managementUserRep.findAll().get(1).getPermissions().get(0).getPermissionName(), "Noob");
		assertEquals(permissionRep.findAll().get(0).getManagementUsers().size(), 1);
		assertEquals(permissionRep.findAll().get(1).getManagementUsers().size(), 1);
	}

	@Test
	@Transactional
	void updateManagementUserTest() {
		addManagementUsers();
		assertEquals(managementUserRep.findById("User1").orElse(null).getUserEmail(), "u1@gmail.com");
		assertEquals(managementUserRep.findById("User2").orElse(null).getUserEmail(), "User2@yahoo.com");
		updateManagementUsers();
		assertEquals(managementUserRep.findById("User1").orElse(null).getUserEmail(), "user1@gmail.com");
		assertEquals(managementUserRep.findById("User2").orElse(null).getUserEmail(), "user2@gmail.com");
	}

	@Test
	@Transactional
	void deleteManagementUserTest() {
		addManagementUsers();
		deleteManagementUsers();
		assertEquals(managementUserRep.count(), 1);
		managementUserRep.deleteAll();
		assertEquals(managementUserRep.count(), 0);
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
	void addExpeesToExpTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		addExperimentees(e);
		assertEquals(experimenteeRep.count(), 2);
		assertEquals(participantRep.count(), 2);
	}

	@Test
	@Transactional
	void updateExpeesTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		addExperimentees(e);
		Experimentee expee1 = experimenteeRep.findAll().get(0);
		Experimentee expee2 = experimenteeRep.findAll().get(1);
		assertEquals(experimenteeRep.findById("123asd").orElse(null).getExperimenteeEmail(), "a@a.com");
		assertEquals(experimenteeRep.findById("vxcf").orElse(null).getExperimenteeEmail(), "b@a.com");
		expee1.setExperimenteeEmail("aa@aa.com");
		expee2.setExperimenteeEmail("bb@bb.com");
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		assertEquals(experimenteeRep.findById("123asd").orElse(null).getExperimenteeEmail(), "aa@aa.com");
		assertEquals(experimenteeRep.findById("vxcf").orElse(null).getExperimenteeEmail(), "bb@bb.com");
	}

	@Test
	@Transactional
	void deleteExpeesTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		addExperimentees(e);
		experimenteeRep.deleteById("123asd");
		assertEquals(experimenteeRep.count(), 1);
		assertEquals(experimenteeRep.findAll().get(0).getAccessCode(), "vxcf");
		experimenteeRep.deleteAll();
		assertEquals(experimenteeRep.count(), 0);
	}


	private void addExperimentees(Experiment e) {
		Experimentee expee1 = new Experimentee("123asd", "a@a.com"),
				expee2 = new Experimentee("vxcf", "b@a.com");
		Participant p1 = new Participant(e),
				p2 = new Participant(e);
		expee1.setParticipant(p1);
		expee2.setParticipant(p2);
		participantRep.save(p1);
		participantRep.save(p2);
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
	}

	@Test
	@Transactional
	void addGradingTasksTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		createStagesForGradingTasks(e);
		assertEquals(stageRep.count(), 2);
		createGradingTask(e);
		assertEquals(gradingTaskRep.count(), 2);
		assertEquals(gradingTaskRep.findAll().get(0).getStages().size(), 1);
	}

	private void createGradingTask(Experiment baseExp) {
		Experiment gradingExp = new Experiment("gradingExp");
		experimentRep.save(gradingExp);
		List<Stage> stagesForGT1 = new ArrayList<>();
		stagesForGT1.add(stageRep.findAll().get(0));
		GradingTask gt1 = new GradingTask("gt1", baseExp, null, gradingExp, stagesForGT1);
		gradingTaskRep.save(gt1);
		List<Stage> stagesForGT2 = new ArrayList<>();
		stagesForGT2.add(stageRep.findAll().get(0));
		GradingTask gt2 = new GradingTask("gt2", baseExp, null, gradingExp, stagesForGT2);
		gradingTaskRep.save(gt2);
	}

	@Test
	@Transactional
	void assignGradersToGradingTaskTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		createStagesForGradingTasks(e);
		createGradingTask(e);
		createGradersForGradingTasks(e);
		assertEquals(graderRep.count(), 2);
		assignGradersToGradingTasks();
		assertEquals(graderToGradingTaskRep.count(), 2);
	}

	private void assignGradersToGradingTasks() {
		Grader g1 = graderRep.findAll().get(0), g2 = graderRep.findAll().get(1);
		GradingTask gt1 = gradingTaskRep.findAll().get(0), gt2 = gradingTaskRep.findAll().get(1);
		GraderToGradingTask graderToGradingTask1 = new GraderToGradingTask(gt1, g1, "1");
		GraderToGradingTask graderToGradingTask2 = new GraderToGradingTask(gt2, g2, "2");
		graderToGradingTaskRep.save(graderToGradingTask1);
		assertEquals(graderToGradingTaskRep.count(), 1);
		graderToGradingTaskRep.save(graderToGradingTask2);
	}

	@Test
	@Transactional
	void addExpeesToGradingTasksTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		createStagesForGradingTasks(e);
		createGradingTask(e);
		createExperimenteesForGradingTasks(e);
		assertEquals(experimenteeRep.count(), 2);
		createGradersForGradingTasks(e);
		assignGradersToGradingTasks();
		assignExpeesToGradingTasks();
		assertEquals(graderRep.findAll().get(0).getAssignedGradingTasks().size(), 1);
		assertEquals(gradingTaskRep.findAll().get(0).getAssignedGradingTasks().size(), 1);
		GradersGTToParticipants participant1Gt1 = gradersGTToParticipantsRep.findAll().get(0);
		participant1Gt1.setGradingState(true);
		gradersGTToParticipantsRep.save(participant1Gt1);
		assertEquals(gradersGTToParticipantsRep.findAll().get(0).getGradingState(), true);
	}

	private void assignExpeesToGradingTasks() {
		GraderToGradingTask graderToGradingTask1 = graderToGradingTaskRep.findAll().get(0);
		GraderToGradingTask graderToGradingTask2 = graderToGradingTaskRep.findAll().get(1);
		GradersGTToParticipants participant1Gt1 = new GradersGTToParticipants(graderToGradingTask1, participantRep.findAll().get(0));
		GradersGTToParticipants participant1Gt2 = new GradersGTToParticipants(graderToGradingTask2, participantRep.findAll().get(1));
		gradersGTToParticipantsRep.save(participant1Gt1);
		gradersGTToParticipantsRep.save(participant1Gt2);
		assertEquals(gradersGTToParticipantsRep.count(), 2);
		graderToGradingTaskRep.save(graderToGradingTask1);
		graderToGradingTaskRep.save(graderToGradingTask2);
		Grader g1 = graderRep.findAll().get(0);
		Grader g2 = graderRep.findAll().get(1);
		graderRep.save(g1);
		graderRep.save(g2);
		GradingTask gt1 = gradingTaskRep.findAll().get(0), gt2 = gradingTaskRep.findAll().get(1);
		gradingTaskRep.save(gt1);
		gradingTaskRep.save(gt2);
	}

	private void createStagesForGradingTasks(Experiment e) {
		InfoStage infoStage1 = new InfoStage("hi", e);
		stageRep.save(infoStage1);
		experimentRep.save(e);
		InfoStage infoStage2 = new InfoStage("bye", e);
		stageRep.save(infoStage2);
		experimentRep.save(e);
	}

	private void createExperimenteesForGradingTasks(Experiment e) {
		Experimentee expee1 = new Experimentee("123asd", "a@a.com");
		Participant pOfExpee1 = new Participant(e);
		expee1.setParticipant(pOfExpee1);
		participantRep.save(pOfExpee1);
		experimenteeRep.save(expee1);
		Experimentee expee2 = new Experimentee("aaa", "b@b.com");
		Participant pOfExpee2 = new Participant(e);
		expee2.setParticipant(pOfExpee2);
		participantRep.save(pOfExpee2);
		experimenteeRep.save(expee2);
	}

	private void createGradersForGradingTasks(Experiment e) {
		Participant pOfGrader1 = new Participant(e);
		participantRep.save(pOfGrader1);
		Grader g1 = new Grader("mosh@gmail.com", pOfGrader1);
		graderRep.save(g1);
		Participant pOfGrader2 = new Participant(e);
		participantRep.save(pOfGrader2);
		Grader g2 = new Grader("shalom@gmail.com", pOfGrader2);
		graderRep.save(g2);
	}

	@Test
	@Transactional
	void addStagesTest() {
		addExperiments();
		assignStagesToExperiments();
		assertEquals(stageRep.count(), 10);
		assertEquals(infoStageRep.findAll().get(0).getInfo(), "hello");
	}

	@Test
	@Transactional
	void updateStagesTest() {
		addExperiments();
		assignStagesToExperiments();
		InfoStage example = infoStageRep.findAll().get(0);
		assertEquals(example.getInfo(), "hello");
		example.setInfo("hi there");
		stageRep.save(example);
		assertEquals(infoStageRep.findAll().get(0).getInfo(), "hi there");
		CodeStage codeStage = codeStageRep.findAll().get(0);
		assertEquals(codeStage.getTemplate(), "");
		codeStage.setTemplate("int main() {");
		stageRep.save(codeStage);
		assertEquals(codeStageRep.findAll().get(0).getTemplate(), "int main() {");
	}

	@Test
	@Transactional
	void deleteStagesTest() {
		addExperiments();
		assignStagesToExperiments();
		InfoStage stage = infoStageRep.findAll().get(0);
		assertEquals(stageRep.count(), 10);
		infoStageRep.delete(stage);
		assertEquals(stageRep.count(), 9);
		assertEquals(infoStageRep.count(), 3);
		stageRep.deleteAll();
		assertEquals(stageRep.count(), 0);
		assertEquals(infoStageRep.count(), 0);
		assertEquals(codeStageRep.count(), 0);
	}

	@Test
	@Transactional
	void addAndUpdateQuestionsToStageTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		List<JSONObject> questions = createQuestionJsons();
		QuestionnaireStage s = new QuestionnaireStage(e);
		stageRep.save(s);
		createAndSaveQuestions(questions, s);
		assertEquals(questionRep.count(), 2);
		assertEquals(questionnaireStageRep.findAll().get(0).getQuestions().size(), 2);
		JSONObject updatedJsonQuestion = new JSONObject();
		updatedJsonQuestion.put("how old are you?", new String[] {"10-20", "21-30", "31-40", "41+"});
		Question q = s.getQuestions().get(0);
		q.setQuestionJson(updatedJsonQuestion.toJSONString());
		stageRep.save(s);
		questionRep.save(q);
		assertEquals(questionRep.count(), 2);
		assertEquals(questionRep.findById(q.getQuestionID()).orElse(null).getQuestionJson(), updatedJsonQuestion.toJSONString());
	}

	private List<JSONObject> createQuestionJsons() {
		JSONObject jsonQuestion1 = new JSONObject();
		jsonQuestion1.put("how old are you?", new String[] {"10-20", "20-30", "30-40", "40+"});
		JSONObject jsonQuestion2 = new JSONObject();
		jsonQuestion2.put("favorite programming language?", new String[] {"c", "c++", "java", "python"});
		List<JSONObject> questions = new ArrayList<>();
		questions.add(jsonQuestion1);
		questions.add(jsonQuestion2);
		return questions;
	}

	private void createAndSaveQuestions(List<JSONObject> questions, QuestionnaireStage s) {
		int QIdx = 1;
		for (JSONObject JQuestion : questions) {
			Question q = new Question(QIdx, s, JQuestion.toJSONString());
			questionRep.save(q);
			s.addQuestion(q);
			QIdx++;
		}
		stageRep.save(s);
	}

	@Test
	@Transactional
	void addAndUpdateAnswersTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		List<JSONObject> questions = createQuestionJsons();
		QuestionnaireStage s = new QuestionnaireStage(e);
		stageRep.save(s);
		createAndSaveQuestions(questions, s);
		Participant p1 = createExpeeAndParticipant();
		addAnswers(s, p1);
		assertEquals(answerRep.count(), 2);
		Answer answer = answerRep.findAll().get(0);
		assertEquals(answerRep.findAll().get(0).getNumeralAnswer().intValue(), 2);
		answer.setNumeralAnswer(3);
		answerRep.save(answer);
		assertEquals(answerRep.findAll().get(0).getNumeralAnswer().intValue(), 3);
	}

	@Test
	@Transactional
	void deleteQuestionsAndAnswersTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		List<JSONObject> questions = createQuestionJsons();
		QuestionnaireStage s = new QuestionnaireStage(e);
		stageRep.save(s);
		createAndSaveQuestions(questions, s);
		Participant p1 = createExpeeAndParticipant();
		addAnswers(s, p1);
		Question toRemove = s.getQuestions().get(0);
		s.getQuestions().remove(toRemove);
		stageRep.save(s);
		answerRep.delete(answerRep.findAll().get(0));
		assertEquals(answerRep.count(), 1);
		questionRep.deleteById(toRemove.getQuestionID());
		assertEquals(questionRep.count(), 1);
		answerRep.deleteAll();
		questionRep.deleteAll();
		assertEquals(answerRep.count(), 0);
		assertEquals(questionRep.count(), 0);
	}

	private void addAnswers(QuestionnaireStage s, Participant p1) {
		Answer answer1 = new Answer(2, s.getQuestions().get(0), p1);
		answerRep.save(answer1);
		Answer answer2 = new Answer(3, s.getQuestions().get(1), p1);
		answerRep.save(answer2);
	}

	private Participant createExpeeAndParticipant() {
		Experimentee expee1 = new Experimentee("123asd", "a@a.com");
		Participant p1 = new Participant(experimentRep.findAll().get(0));
		experimentRep.findAll().get(0).getParticipants().add(p1);
		expee1.setParticipant(p1);
		participantRep.save(p1);
		experimenteeRep.save(expee1);
		return p1;
	}

	@Test
	@Transactional
	void addAndUpdateRequirementsForExpTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		CodeStage codeStage = new CodeStage("welcome", "", e);
		stageRep.save(codeStage);
		assertEquals(codeStageRep.findAll().get(0).getRequirements().size(), 0);
		addRequirementsToStage(codeStage);
		assertEquals(requirementRep.count(), 2);
		assertEquals(requirementRep.findAll().get(0).getText(), "write a function that prints Hello World!");
		codeStage.getRequirements().get(0).setText("please blabla write a function that prints Hello World!");
		requirementRep.save(codeStage.getRequirements().get(0));
		stageRep.save(codeStage);
		assertEquals(requirementRep.findAll().get(0).getText(), "please blabla write a function that prints Hello World!");
		assertEquals(codeStageRep.findAll().get(0).getRequirements().get(0).getText(), "please blabla write a function that prints Hello World!");
		assertEquals(codeStageRep.findAll().get(0).getRequirements().size(), 2);
	}

	@Test
	@Transactional
	void addAndUpdateCodeResultForExpeeTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		CodeStage codeStage = new CodeStage("welcome", "", e);
		stageRep.save(codeStage);
		Participant p1 = createExpeeAndParticipant();
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
	}

	@Test
	@Transactional
	void addAndUpdateTaggingStageForExpeeTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		CodeStage codeStage = new CodeStage("welcome", "", e);
		stageRep.save(codeStage);
		addRequirementsToStage(codeStage);
		Participant p1 = createExpeeAndParticipant();
		CodeResult codeResult = new CodeResult(p1, codeStage, "System.out.println(\"hello world\");");
		codeResultRep.save(codeResult);
		TaggingStage taggingStage = new TaggingStage(codeStage, e);
		stageRep.save(taggingStage);
		assertEquals(stageRep.count(), 2);
		addRequirementTags(codeStage, p1);
		assertEquals(requirementTagRep.count(), 2);
		assertEquals(requirementTagRep.findAll().get(0).getLength(), 10);
		RequirementTag req = requirementTagRep.findAll().get(0);
		req.setLength(13);
		requirementTagRep.save(req);
		assertEquals(requirementTagRep.count(), 2);
		assertEquals(requirementTagRep.findAll().get(0).getLength(), 13);
	}

	@Test
	@Transactional
	void deleteReqsAndExpeeResultTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		CodeStage codeStage = new CodeStage("welcome", "", e);
		stageRep.save(codeStage);
		addRequirementsToStage(codeStage);
		Participant p1 = createExpeeAndParticipant();
		CodeResult codeResult = new CodeResult(p1, codeStage, "System.out.println(\"hello world\");");
		codeResultRep.save(codeResult);
		TaggingStage taggingStage = new TaggingStage(codeStage, e);
		stageRep.save(taggingStage);
		addRequirementTags(codeStage, p1);
		requirementTagRep.deleteAll();
		assertEquals(requirementTagRep.count(), 0);
		Requirement toRem = codeStage.getRequirements().get(1);
		codeStage.getRequirements().remove(1);
		requirementRep.deleteById(toRem.getRequirementID());
		assertEquals(requirementRep.count(), 1);
		stageRep.save(codeStage);
		assertEquals(codeStageRep.findAll().get(0).getRequirements().size(), 1);
		codeResultRep.deleteById(codeResult.getCodeResultID());
		assertEquals(codeResultRep.count(), 0);
	}

	private void addRequirementTags(CodeStage codeStage, Participant p1) {
		List<Requirement> requirementsFor_rt1 = new ArrayList<>();
		requirementsFor_rt1.add(codeStage.getRequirements().get(0));
		requirementsFor_rt1.add(codeStage.getRequirements().get(1));
		RequirementTag rt1 = new RequirementTag(0, 10, p1, requirementsFor_rt1);
		List<Requirement> requirementsFor_rt2 = new ArrayList<>();
		requirementsFor_rt2.add(codeStage.getRequirements().get(0));
		RequirementTag rt2 = new RequirementTag(10, 15, p1, requirementsFor_rt2);
		requirementTagRep.save(rt1);
		requirementTagRep.save(rt2);
	}

	private void addRequirementsToStage(CodeStage codeStage) {
		Requirement r1 = new Requirement(codeStage, "write a function that prints Hello World!");
		codeStage.addRequirement(r1);
		requirementRep.save(r1);
		stageRep.save(codeStage);
		Requirement r2 = new Requirement(codeStage, "function must have 1 line");
		codeStage.addRequirement(r2);
		requirementRep.save(r2);
		stageRep.save(codeStage);
	}

	private void addExperiments() {
		Experiment e1 = new Experiment("Exp1"),
				e2 = new Experiment("Expr2");
		experimentRep.save(e1);
		experimentRep.save(e2);
	}

	private void assignStagesToExperiments() {
		InfoStage s11 = new InfoStage("hello", experimentRep.findAll().get(0));
		stageRep.save(s11);
		QuestionnaireStage s21 = new QuestionnaireStage(experimentRep.findAll().get(0));
		stageRep.save(s21);
		InfoStage s31 = new InfoStage("good luck", experimentRep.findAll().get(0));
		stageRep.save(s31);
		CodeStage s41 = new CodeStage("enter your code", "", experimentRep.findAll().get(0));
		stageRep.save(s41);
		TaggingStage s51 = new TaggingStage(s41, experimentRep.findAll().get(0));
		stageRep.save(s51);
		InfoStage s12 = new InfoStage("hello", experimentRep.findAll().get(1));
		stageRep.save(s12);
		QuestionnaireStage s22 = new QuestionnaireStage(experimentRep.findAll().get(1));
		stageRep.save(s22);
		InfoStage s32 = new InfoStage("good luck", experimentRep.findAll().get(1));
		stageRep.save(s32);
		CodeStage s42 = new CodeStage("enter your code", "", experimentRep.findAll().get(1));
		stageRep.save(s42);
		TaggingStage s52 = new TaggingStage(s41, experimentRep.findAll().get(1));
		stageRep.save(s52);
	}
}