package com.example.demo;

import com.example.demo.Entities.*;
import com.example.demo.Entities.Stages.QuestionnaireStage;
import com.example.demo.Entities.Stages.Stage;
import com.example.demo.Reps.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
class DemoApplicationTests {

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

	@BeforeEach
	void clean() {
		JpaRepository[] reps = {
				managementUserRep,
				experimenteeRep,
				graderRep,
				participantRep,
				stageRep,
				experimentRep,
				permissionRep
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
		assertEquals(managementUserRep.findById("User1").orElse(null).getUser_email(), "user1@gmail.com");
		assertEquals(managementUserRep.findById("User2").orElse(null).getUser_email(), "user2@gmail.com");
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
		managementUser1.setUser_email("user1@gmail.com");
		ManagementUser managementUser2 = managementUserRep.findById("User2").orElse(null);
		managementUser2.setUser_email("user2@gmail.com");
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
		expee1.setExperimentee_email("aa@aa.com");
		expee2.setExperimentee_email("bb@bb.com");
		expee3.setExperimentee_email("cc@cc.com");
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		experimenteeRep.save(expee3);
		assertEquals(experimenteeRep.findById("123asd").orElse(null).getExperimentee_email(), "aa@aa.com");
		assertEquals(experimenteeRep.findById("vxcf").orElse(null).getExperimentee_email(), "bb@bb.com");
		assertEquals(experimenteeRep.findById("asdiiji").orElse(null).getExperimentee_email(), "cc@cc.com");
		experimenteeRep.deleteAll();
		assertEquals(experimenteeRep.count(), 0);
	}

//	@Test
//	@Transactional
//	void stagesCRUDTest() {
//		Experiment e = new Experiment("hi");
//		experimentRep.save(e);
//		Stage s1 = new QuestionnaireStage(e, 1),
//				s2 = new QuestionnaireStage(e, 2);
//		stageRep.save(s1);
//		stageRep.save(s2);
//		assertEquals(stageRep.count(), 2);
//		s1.se("aa@aa.com");
//		s2.setExperimentee_email("bb@bb.com");
//		experimenteeRep.save(expee1);
//		experimenteeRep.save(expee2);
//		experimenteeRep.save(expee3);
//		assertEquals(experimenteeRep.findById("123asd").orElse(null).getExperimentee_email(), "aa@aa.com");
//		assertEquals(experimenteeRep.findById("vxcf").orElse(null).getExperimentee_email(), "bb@bb.com");
//		assertEquals(experimenteeRep.findById("asdiiji").orElse(null).getExperimentee_email(), "cc@cc.com");
//		experimenteeRep.deleteAll();
//		assertEquals(experimenteeRep.count(), 0);
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
		assignStagesToExperiments();
		assertEquals(stageRep.count(), 2);
		assertEquals(experimentRep.findAll().get(0).getStages().size(), 1);
		assertEquals(experimentRep.findAll().get(1).getStages().size(), 1);
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
		Stage s1 = new QuestionnaireStage(experimentRep.findAll().get(0), 1),
				s2 = new QuestionnaireStage(experimentRep.findAll().get(1), 2);
		experimentRep.findAll().get(0).getStages().add(s1);
		experimentRep.findAll().get(1).getStages().add(s2);
		stageRep.save(s1);
		stageRep.save(s2);
	}
}
