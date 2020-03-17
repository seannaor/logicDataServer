package com.example.demo;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.Stages.*;
import com.example.demo.DataAccessLayer.Reps.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
	TaggingStageRep taggingStageRep;
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
				taggingStageRep,
				answerRep,
				codeStageRep,
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
	void answersCRUDTest() {

	}

//	@Test
//	@Transactional
//	void stagesCRUDTest() {
//		Experiment e = new Experiment("hi");
//		experimentRep.save(e);
//		Stage s1 = new InfoStage(e, 1);
//		stageRep.save(s1);
//		Experiment ex = experimentRep.findAll().get(0);
//		Stage x = stageRep.findAll().get(0);
//		((InfoStage)s1).setInfo("aa");
//		infoStageRep.save((InfoStage)s1);
//		String a = infoStageRep.findAll().get(0).getInfo();
//	}

	@Test
	@Transactional
	void createExperimentTest() {
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
//		assignStagesToExperiments();
//		assertEquals(stageRep.count(), 10);
//		assertEquals(experimentRep.findAll().get(0).getStages().size(), 5);
//		assertEquals(experimentRep.findAll().get(1).getStages().size(), 5);
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

//	private void assignStagesToExperiments() {
//		Stage s11 = new InfoStage(experimentRep.findAll().get(0), 1),
//				s21 = new QuestionnaireStage(experimentRep.findAll().get(0), 2),
//				s31 = new InfoStage(experimentRep.findAll().get(0), 3),
//				s41 = new CodeStage(experimentRep.findAll().get(0), 4),
//				s51 = new TaggingStage(experimentRep.findAll().get(0), 5);
//		Stage s12 = new InfoStage(experimentRep.findAll().get(1), 1),
//				s22 = new QuestionnaireStage(experimentRep.findAll().get(1), 2),
//				s32 = new InfoStage(experimentRep.findAll().get(1), 3),
//				s42 = new CodeStage(experimentRep.findAll().get(1), 4),
//				s52 = new TaggingStage(experimentRep.findAll().get(1), 5);
//		stageRep.save(s11);
//
//		((InfoStage)s11).setInfo("aa");
//		((InfoStage)s31).setInfo("bb");
//		((CodeStage)s41).setDescription("hello");
//		((CodeStage)s41).setTemplate("public static void main(string[] args) {");
//		((TaggingStage)s51).setCodeStage((CodeStage)s41);
//		((InfoStage)s12).setInfo("aa");
//		((InfoStage)s32).setInfo("bb");
//		((CodeStage)s42).setDescription("hello");
//		((CodeStage)s42).setTemplate("public static void main(string[] args) {");
//		((TaggingStage)s52).setCodeStage((CodeStage)s42);
//		experimentRep.findAll().get(0).getStages().add(s11);
//		experimentRep.findAll().get(0).getStages().add(s21);
//		experimentRep.findAll().get(0).getStages().add(s31);
//		experimentRep.findAll().get(0).getStages().add(s41);
//		experimentRep.findAll().get(0).getStages().add(s51);
//		experimentRep.findAll().get(1).getStages().add(s12);
//		experimentRep.findAll().get(1).getStages().add(s22);
//		experimentRep.findAll().get(1).getStages().add(s32);
//		experimentRep.findAll().get(1).getStages().add(s42);
//		experimentRep.findAll().get(1).getStages().add(s52);
//		stageRep.save(s11);
//		infoStageRep.save((InfoStage)s11);
//		stageRep.save(s21);
//		questionnaireStageRep.save((QuestionnaireStage)s21);
//		stageRep.save(s31);
//		infoStageRep.save((InfoStage)s31);
//		stageRep.save(s41);
//		codeStageRep.save((CodeStage) s41);
//		stageRep.save(s51);
//		taggingStageRep.save((TaggingStage) s51);
//		stageRep.save(s12);
//		infoStageRep.save((InfoStage)s12);
//		stageRep.save(s22);
//		questionnaireStageRep.save((QuestionnaireStage)s22);
//		stageRep.save(s32);
//		infoStageRep.save((InfoStage)s32);
//		stageRep.save(s42);
//		codeStageRep.save((CodeStage) s42);
//		stageRep.save(s52);
//		codeStageRep.save((CodeStage) s42);
//	}
}