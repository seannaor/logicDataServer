package com.example.demo;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.*;
import com.example.demo.BusinessLayer.Entities.Stages.*;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.DataAccessLayer.Reps.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
	TaggingStageRep taggingStageRep;
	@Autowired
	RequirementRep requirementRep;
	@Autowired
	RequirementTagRep requirementTagRep;
	@Autowired
	GradingTaskRep gradingTaskRep;
	@Autowired
	GraderToGradingTaskRep graderToGradingTaskRep;
	@Autowired
	GraderToParticipantRep graderToParticipantRep;
	@Autowired
	ManagementUserToExperimentRep managementUserToExperimentRep;
	@Autowired
	ResultRep resultRep;

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
				taggingStageRep,
				questionRep,
				answerRep,
				codeResultRep,
				requirementRep,
				requirementTagRep,
				gradingTaskRep,
				graderToGradingTaskRep,
				graderToParticipantRep,
				managementUserToExperimentRep,
				resultRep
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
		List<UUID> codesCreated = addExperimentees(e);
		Experimentee expee1 = experimenteeRep.findById(codesCreated.get(0)).orElse(null);
		Experimentee expee2 = experimenteeRep.findById(codesCreated.get(1)).orElse(null);
		assertEquals(experimenteeRep.findById(expee1.getAccessCode()).orElse(null).getExperimenteeEmail(), "a@a.com");
		assertEquals(experimenteeRep.findById(expee2.getAccessCode()).orElse(null).getExperimenteeEmail(), "b@a.com");
		expee1.setExperimenteeEmail("aa@aa.com");
		expee2.setExperimenteeEmail("bb@bb.com");
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		assertEquals(experimenteeRep.findById(expee1.getAccessCode()).orElse(null).getExperimenteeEmail(), "aa@aa.com");
		assertEquals(experimenteeRep.findById(expee2.getAccessCode()).orElse(null).getExperimenteeEmail(), "bb@bb.com");
	}

	@Test
	@Transactional
	void deleteExpeesTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		addExperimentees(e);
		experimenteeRep.deleteById(experimenteeRep.findAll().get(0).getAccessCode());
		assertEquals(experimenteeRep.count(), 1);
		experimenteeRep.deleteAll();
		assertEquals(experimenteeRep.count(), 0);
	}


	private List<UUID> addExperimentees(Experiment e) {
		Experimentee expee1 = new Experimentee("a@a.com"),
				expee2 = new Experimentee("b@a.com");
		Participant p1 = new Participant(e),
				p2 = new Participant(e);
		expee1.setParticipant(p1);
		expee2.setParticipant(p2);
		participantRep.save(p1);
		participantRep.save(p2);
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		return new ArrayList<>(Arrays.asList(expee1.getAccessCode(), expee2.getAccessCode()));
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
		Experiment generalExp = new Experiment("generalExp");
		experimentRep.save(generalExp);
		List<Stage> stagesForGT1 = new ArrayList<>();
		stagesForGT1.add(stageRep.findAll().get(0));
		GradingTask gt1 = new GradingTask("gt1", baseExp, generalExp, gradingExp, stagesForGT1);
		gradingTaskRep.save(gt1);
		List<Stage> stagesForGT2 = new ArrayList<>();
		stagesForGT2.add(stageRep.findAll().get(0));
		GradingTask gt2 = new GradingTask("gt2", baseExp, generalExp, gradingExp, stagesForGT2);
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
		GraderToGradingTask graderToGradingTask1 = new GraderToGradingTask(gt1, g1);
		GraderToGradingTask graderToGradingTask2 = new GraderToGradingTask(gt2, g2);
		graderToGradingTask1.setGraderAccessCode(UUID.randomUUID());
		graderToGradingTask2.setGraderAccessCode(UUID.randomUUID());
		participantRep.save(graderToGradingTask1.getGeneralExpParticipant());
		graderToGradingTaskRep.save(graderToGradingTask1);
		participantRep.save(graderToGradingTask2.getGeneralExpParticipant());
		graderToGradingTaskRep.save(graderToGradingTask2);
	}

	@Test
	@Transactional
	void addExpeesToGradingTasksTest() throws ExistException {
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
		GraderToParticipant participant1Gt1 = graderToParticipantRep.findAll().get(0);
		participant1Gt1.setGradingState(true);
		graderToParticipantRep.save(participant1Gt1);
		assertEquals(graderToParticipantRep.findAll().get(0).getGradingState(), true);
	}

	private void assignExpeesToGradingTasks() throws ExistException {
		GraderToGradingTask graderToGradingTask1 = graderToGradingTaskRep.findAll().get(0);
		GraderToGradingTask graderToGradingTask2 = graderToGradingTaskRep.findAll().get(1);
		GraderToParticipant participant1Gt1 = new GraderToParticipant(graderToGradingTask1, participantRep.findAll().get(0));
		GraderToParticipant participant1Gt2 = new GraderToParticipant(graderToGradingTask2, participantRep.findAll().get(1));
		participantRep.save(participant1Gt1.getGraderParticipant());
		graderToParticipantRep.save(participant1Gt1);
		participantRep.save(participant1Gt2.getGraderParticipant());
		graderToParticipantRep.save(participant1Gt2);
		assertEquals(graderToParticipantRep.count(), 2);
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
		InfoStage infoStage1 = new InfoStage("hi");
		infoStage1.setExperiment(e);
		stageRep.save(infoStage1);
		experimentRep.save(e);
		InfoStage infoStage2 = new InfoStage("bye");
		infoStage2.setExperiment(e);
		stageRep.save(infoStage2);
		experimentRep.save(e);
	}

	private void createExperimenteesForGradingTasks(Experiment e) {
		Experimentee expee1 = new Experimentee("a@a.com");
		Participant pOfExpee1 = new Participant(e);
		expee1.setParticipant(pOfExpee1);
		participantRep.save(pOfExpee1);
		experimenteeRep.save(expee1);
		Experimentee expee2 = new Experimentee("b@b.com");
		Participant pOfExpee2 = new Participant(e);
		expee2.setParticipant(pOfExpee2);
		participantRep.save(pOfExpee2);
		experimenteeRep.save(expee2);
	}

	private void createGradersForGradingTasks(Experiment e) {
		Grader g1 = new Grader("mosh@gmail.com");
		graderRep.save(g1);
		Grader g2 = new Grader("shalom@gmail.com");
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
		QuestionnaireStage s = new QuestionnaireStage();
		s.setExperiment(e);
		stageRep.save(s);
		createAndSaveQuestions(questions, s);
		assertEquals(questionRep.count(), 2);
		assertEquals(questionnaireStageRep.findAll().get(0).getQuestions().size(), 2);
		JSONObject updatedJsonQuestion = new JSONObject();
		List<String> answers1 = new ArrayList<>();
		answers1.add("10-20");answers1.add("21-30");answers1.add("31-40");answers1.add("41+");
		updatedJsonQuestion.put("how old are you?", answers1);
		Question q = s.getQuestions().get(0);
		q.setQuestionJson(updatedJsonQuestion.toString());
		stageRep.save(s);
		questionRep.save(q);
		assertEquals(questionRep.count(), 2);
		assertEquals(questionRep.findById(q.getQuestionID()).orElse(null).getQuestionJson(), updatedJsonQuestion);
	}

	private List<JSONObject> createQuestionJsons() {
		JSONObject jsonQuestion1 = new JSONObject();
		List<String> answers1 = new ArrayList<>();
		answers1.add("10-20");answers1.add("20-30");answers1.add("30-40");answers1.add("40+");
		jsonQuestion1.put("how old are you?", answers1);
		JSONObject jsonQuestion2 = new JSONObject();
		List<String> answers2 = new ArrayList<>();
		answers2.add("c");answers2.add("c++");answers2.add("java");answers2.add("python");
		jsonQuestion2.put("favorite programming language?", answers2);
		List<JSONObject> questions = new ArrayList<>();
		questions.add(jsonQuestion1);
		questions.add(jsonQuestion2);
		return questions;
	}

	private void createAndSaveQuestions(List<JSONObject> questions, QuestionnaireStage s) {
		int QIdx = 1;
		for (JSONObject JQuestion : questions) {
			Question q = new Question(JQuestion.toString());
			q.setQuestionnaireStage(s);
			q.setQuestionIndex(QIdx);
			questionRep.save(q);
			s.addQuestion(q);
			QIdx++;
		}
		stageRep.save(s);
	}

	@Test
	@Transactional
	void addAndUpdateAnswersTest() throws ParseException {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		List<JSONObject> questions = createQuestionJsons();
		QuestionnaireStage s = new QuestionnaireStage();
		s.setExperiment(e);
		stageRep.save(s);
		createAndSaveQuestions(questions, s);
		Participant p1 = createExpeeAndParticipant();
		addAnswers(s, p1);
		assertEquals(resultRep.count(), 1);
		assertEquals(((QuestionnaireResult)resultRep.findAll().get(0)).getAnswers().size(), 2);
		assertEquals(answerRep.count(), 2);
		Answer answer = answerRep.findAll().get(0);
		assertEquals(answer.getAnswer(), "2");
		answer.setAnswer("4");
		answerRep.save(answer);
		assertEquals(answerRep.findAll().get(0).getAnswer(), "4");
	}

	@Test
	@Transactional
	void deleteQuestionsAndAnswersTest() {
		Experiment e = new Experiment("hi");
		experimentRep.save(e);
		List<JSONObject> questions = createQuestionJsons();
		QuestionnaireStage s = new QuestionnaireStage();
		s.setExperiment(e);
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
		QuestionnaireResult q = new QuestionnaireResult();
		q.setStageAndParticipant(questionnaireStageRep.findAll().get(0), p1);
		resultRep.save(q);
		Answer answer1 = new Answer("2", s.getQuestions().get(0));
		answer1.setQuestionnaireResult(q);
		q.addAns(answer1);
		answerRep.save(answer1);
		resultRep.save(q);
		Answer answer2 = new Answer("3", s.getQuestions().get(1));
		answer2.setQuestionnaireResult(q);
		q.addAns(answer2);
		answerRep.save(answer2);
		resultRep.save(q);
	}

	private Participant createExpeeAndParticipant() {
		Experimentee expee1 = new Experimentee( "a@a.com");
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
		CodeStage codeStage = new CodeStage("welcome", "", new ArrayList<>(),"JAVA");
		codeStage.setExperiment(e);
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
		CodeStage codeStage = new CodeStage("welcome", "", new ArrayList<>(),"JAVA");
		codeStage.setExperiment(e);
		stageRep.save(codeStage);
		Participant p1 = createExpeeAndParticipant();
		assertEquals(participantRep.count(), 1);
		assertEquals(experimenteeRep.count(), 1);
		CodeResult codeResult = new CodeResult("System.out.println(\"hello world\");");
		codeResult.setStageAndParticipant(codeStage, p1);
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
		CodeStage codeStage = new CodeStage("welcome", "", new ArrayList<>(),"JAVA");
		codeStage.setExperiment(e);
		stageRep.save(codeStage);
		addRequirementsToStage(codeStage);
		Participant p1 = createExpeeAndParticipant();
		CodeResult codeResult = new CodeResult("System.out.println(\"hello world\");");
		codeResult.setStageAndParticipant(codeStage, p1);
		codeResultRep.save(codeResult);
		TaggingStage taggingStage = new TaggingStage(codeStage);
		taggingStage.setExperiment(e);
		stageRep.save(taggingStage);
		assertEquals(stageRep.count(), 2);
		TaggingResult t = addRequirementTags(codeStage, p1);
		assertEquals(((TaggingResult)resultRep.findById(t.getResultID()).orElse(null)).getTags().size(), 2);
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
		CodeStage codeStage = new CodeStage("welcome", "", new ArrayList<>(),"JAVA");
		codeStage.setExperiment(e);
		stageRep.save(codeStage);
		addRequirementsToStage(codeStage);
		Participant p1 = createExpeeAndParticipant();
		CodeResult codeResult = new CodeResult("System.out.println(\"hello world\");");
		codeResult.setStageAndParticipant(codeStage, p1);
		codeResultRep.save(codeResult);
		TaggingStage taggingStage = new TaggingStage(codeStage);
		taggingStage.setExperiment(e);
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
		codeResultRep.deleteById(codeResult.getResultID());
		assertEquals(codeResultRep.count(), 0);
	}

	private TaggingResult addRequirementTags(CodeStage codeStage, Participant p1) {
		TaggingResult q = new TaggingResult();
		q.setStageAndParticipant(taggingStageRep.findAll().get(0), p1);
		resultRep.save(q);
		Requirement requirementFor_rt1 = codeStage.getRequirements().get(0);
		RequirementTag rt1 = new RequirementTag(0, 10, requirementFor_rt1);
		rt1.setTaggingResult(q);
		q.addTag(rt1);
		rt1.setCodeStageIdx(codeStage.getStageID().getStageIndex());
		Requirement requirementFor_rt2 = codeStage.getRequirements().get(1);
		RequirementTag rt2 = new RequirementTag(10, 15, requirementFor_rt2);
		rt2.setTaggingResult(q);
		q.addTag(rt2);
		rt2.setCodeStageIdx(codeStage.getStageID().getStageIndex());
		requirementTagRep.save(rt1);
		requirementTagRep.save(rt2);
		resultRep.save(q);
		return q;
	}

	private void addRequirementsToStage(CodeStage codeStage) {
		Requirement r1 = new Requirement("write a function that prints Hello World!");
		r1.setCodeStage(codeStage);
		r1.setRequirementIndex(codeStage.getRequirements().size());
		codeStage.addRequirement(r1);
		requirementRep.save(r1);
		stageRep.save(codeStage);
		Requirement r2 = new Requirement("function must have 1 line");
		r2.setCodeStage(codeStage);
		r2.setRequirementIndex(codeStage.getRequirements().size());
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
		InfoStage s11 = new InfoStage("hello");
		QuestionnaireStage s21 = new QuestionnaireStage();
		InfoStage s31 = new InfoStage("good luck");
		CodeStage s41 = new CodeStage("enter your code", "", new ArrayList<>() ,"JAVA");
		TaggingStage s51 = new TaggingStage(s41);
		for(Stage s : List.of(s11, s21, s31, s41, s51)) {
			s.setExperiment(experimentRep.findAll().get(0));
			stageRep.save(s);
		}
		InfoStage s12 = new InfoStage("hello");
		QuestionnaireStage s22 = new QuestionnaireStage();
		InfoStage s32 = new InfoStage("good luck");
		CodeStage s42 = new CodeStage("enter your code", "", new ArrayList<>() ,"JAVA");
		TaggingStage s52 = new TaggingStage(s42);
		for(Stage s : List.of(s12, s22, s32, s42, s52)) {
			s.setExperiment(experimentRep.findAll().get(1));
			stageRep.save(s);
		}
	}
}
