package com.example.demo.UnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.ManagementUserToExperiment;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.demo.Utils.buildSimpleExp;
import static org.junit.jupiter.api.Assertions.*;

public class ExperimentUnitTest {
    private Experiment experiment;

    @BeforeEach
    public void init() throws FormatException {
        experiment = new Experiment("experiment");
        for (JSONObject stageJ : buildSimpleExp(List.of("what is your name?"))) {
            Stage s = Stage.parseStage(stageJ, experiment);
            experiment.addStage(s);
        }
    }

    @Test
    public void nameTest() {
        String name = "new name";
        experiment.setExperimentName(name);
        assertEquals(name, experiment.getExperimentName());
    }

    @Test
    public void publishTest() {
        experiment.setPublished(true);
        assertTrue(experiment.getPublished());
    }

    @Test
    public void isTaskTest() {
        experiment.setGradingTaskExp(true);
        assertTrue(experiment.isGradingTaskExp());
    }

    @Test
    public void idTest() {
        int id = 10;
        experiment.setExperimentId(id);
        assertEquals(id, experiment.getExperimentId());
    }

    @Test
    public void managerTest() {
        ManagementUser manager = new ManagementUser("name", "password", "mail");
        assertFalse(experiment.containsManger(manager));

        new ManagementUserToExperiment(manager, experiment, "GUEST");

        assertTrue(experiment.containsManger(manager));
    }

    @Test
    public void addParticipant() {
        int participantsCount = experiment.getParticipants().size();
        experiment.addParticipant(new Participant());
        assertEquals(participantsCount + 1, experiment.getParticipants().size());
    }

    @Test
    public void setParticipant() {
        List<Participant> participants = List.of(new Participant());
        experiment.setParticipants(participants);
        assertEquals(participants, experiment.getParticipants());
    }

    @Test
    public void setMangers() {
        ManagementUser manager = new ManagementUser("name", "password", "mail");
        List<ManagementUserToExperiment> managementUserToExperiments = List.of(new ManagementUserToExperiment(manager, experiment, "GUEST"));
        experiment.setManagementUserToExperiments(managementUserToExperiments);
        assertEquals(managementUserToExperiments, experiment.getManagementUserToExperiments());
    }

    @Test
    public void addStage() {
        int stageCount = experiment.getStages().size();
        experiment.addStage(new InfoStage("info"));
        assertEquals(stageCount + 1, experiment.getStages().size());
    }

    @Test
    public void setStages() {
        List<Stage> stages = List.of(new InfoStage("info1"), new InfoStage("info2"));
        experiment.setStages(stages);
        assertEquals(stages, experiment.getStages());
    }
}
