package com.example.demo;

import antlr.TokenStreamHiddenTokenFilter;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.DataAccessLayer.Reps.ExperimentRep;
import com.example.demo.DataAccessLayer.Reps.ExperimenteeRep;
import com.example.demo.DataAccessLayer.Reps.ManagementUserRep;
import com.example.demo.DataAccessLayer.Reps.ParticipantRep;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;


@SpringBootApplication
public class DataInjector {

    @Autowired
    static ManagementUserRep managementUserRep;
    @Autowired
    static ExperimenteeRep experimenteeRep;
    @Autowired
    static ExperimentRep experimentRep;
    @Autowired
    static ParticipantRep participantRep;

    public static void main(String[] args) {
        SpringApplication.run(DataInjector.class, args);
        System.out.println("This class is meant to insert data to the db." +
                "This is so we wont manually insert data." +
                "Add as much data as possible.");
        addManagementUsers();
        addExperiments();
        addParticipants();
        addGradingTasks();
    }

    private static void addManagementUsers() {
        System.out.println("Add management users, permissions");
//        ManagementUser u1 = new ManagementUser("ADMIN", "admin1234", "admin@gmail.com");
//        managementUserRep.save(u1);
    }

    private static void addExperiments() {
        System.out.println("Add experiments, stages, sub stages info " +
                "for example, questions for questionnaire");
    }

    private static void addParticipants() {
        System.out.println("Add participants, most importantly experimentees and their results");
        Experiment e = new Experiment("hi");
        e.setStages(List.of(new InfoStage("fuckkkkk info 1",e),new InfoStage("fuckkkkk info 2",e)));
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
    }

    private static void addGradingTasks() {
        System.out.println("Add graders and grading tasks info");
    }

}