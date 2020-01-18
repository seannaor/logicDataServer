package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRep extends JpaRepository<Stage, Stage.StageID> {
}
