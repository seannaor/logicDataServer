package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataInjector {
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
    }

    private static void addExperiments() {
        System.out.println("Add experiments, stages, sub stages info " +
                "for example, questions for questionnaire");
    }

    private static void addParticipants() {
        System.out.println("Add participants, most importantly experimentees and their results");
    }

    private static void addGradingTasks() {
        System.out.println("Add graders and grading tasks info");
    }

}
