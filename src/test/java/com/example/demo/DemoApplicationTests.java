package com.example.demo;

import com.example.demo.Entities.*;
import com.example.demo.Reps.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
		JpaRepository[] reps = {experimenteeRep, experimentRep, graderRep,
				managementUserRep, participantRep, permissionRep,
				stageRep};
		for (JpaRepository rep : reps)
			rep.deleteAll();
	}

	@Test
	void managementUserTest() {
		ManagementUser u1 = new ManagementUser("User1", "Pass1", "u1@gmail.com"),
				u2 = new ManagementUser("User2", "Password2", "User2@yahoo.com");
		Permission p1 = new Permission("Super Manager"),
				p2 = new Permission("Noob");
		permissionRep.save(p1);
		permissionRep.save(p2);
		u1.getPermissions().add(p1);
		u2.getPermissions().add(p2);
		managementUserRep.save(u1);
		managementUserRep.save(u2);
		assertEquals(managementUserRep.count(), 2);
		assertEquals(permissionRep.count(), 2);
	}

	@Test
	void experimentUserTest() {
		Experiment e1 = new Experiment("Exp1"),
				e2 = new Experiment("Exper2");
		ManagementUser u1 = new ManagementUser("User1", "123Pass", "u1@u1mail.com");
		experimentRep.save(e1);
		experimentRep.save(e2);
		u1.getExperiments().add(e1);
		u1.getExperiments().add(e2);
		Experimentee expee1 = new Experimentee("123asd", "a@a.com"),
				expee2 = new Experimentee("vxcf", "b@a.com"),
				expee3 = new Experimentee("asdiiji", "c@a.com");
		Participant p1 = new Participant(e1),
				p2 = new Participant(e1),
				p3 = new Participant(e2);
		expee1.setParticipant(p1);
		expee2.setParticipant(p2);
		expee3.setParticipant(p3);

		participantRep.save(p1);
		participantRep.save(p2);
		participantRep.save(p3);
		experimenteeRep.save(expee1);
		experimenteeRep.save(expee2);
		experimenteeRep.save(expee3);
		experimentRep.save(e1);
		experimentRep.save(e2);
		managementUserRep.save(u1);
	}

	@Test
	public void stageTest() {
		Experiment e1 = new Experiment("My Exp");
		Stage s1 = new Stage(e1, 1),
				s2 = new Stage(e1, 2);
		experimentRep.save(e1);
		stageRep.save(s1);
		stageRep.save(s2);

		List<Stage> l  = stageRep.findAll();
		assertEquals(l.size(), 2);
	}
}
