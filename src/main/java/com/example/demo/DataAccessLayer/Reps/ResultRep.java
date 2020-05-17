package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Results.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRep extends JpaRepository<Result, Result.ResultID> {
}
