package com.example.demo;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.Results.Answer;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	RequirementTagRep requirementTagRep;

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
				requirementTagRep
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
		assertEquals(questionRep.findAll().get(0).getQuestionJson(), updatedJsonQuestion.toJSONString());
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
		Set<Question> updated = new HashSet<>(s.getQuestions());
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
		s.setQuestions(new HashSet<>());
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
		//CodeStage codeStage = new CodeStage()

	}

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
		CodeStage s2 = new CodeStage("hello", "", new ArrayList<>(), e);
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
		experimentRep.findAll().get(0).getParticipants().add(p1);
		experimentRep.findAll().get(0).getParticipants().add(p2);
		experimentRep.findAll().get(1).getParticipants().add(p3);
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
		CodeStage s41 = new CodeStage("enter your code", "", new ArrayList<>(), experimentRep.findAll().get(0));
		TaggingStage s51 = new TaggingStage(s41, experimentRep.findAll().get(0));
		InfoStage s12 = new InfoStage("hello", experimentRep.findAll().get(1));
		QuestionnaireStage s22 = new QuestionnaireStage(experimentRep.findAll().get(1));
		InfoStage s32 = new InfoStage("good luck", experimentRep.findAll().get(1));
		CodeStage s42 = new CodeStage("enter your code", "", new ArrayList<>(), experimentRep.findAll().get(1));
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
		experimentRep.findAll().get(0).getStages().add(s11);
		experimentRep.findAll().get(0).getStages().add(s21);
		experimentRep.findAll().get(0).getStages().add(s31);
		experimentRep.findAll().get(0).getStages().add(s41);
		experimentRep.findAll().get(0).getStages().add(s51);
		experimentRep.findAll().get(1).getStages().add(s12);
		experimentRep.findAll().get(1).getStages().add(s22);
		experimentRep.findAll().get(1).getStages().add(s32);
		experimentRep.findAll().get(1).getStages().add(s42);
		experimentRep.findAll().get(1).getStages().add(s52);
	}
}