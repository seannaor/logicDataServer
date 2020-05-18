package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeResultRep extends JpaRepository<CodeResult, Result.ResultID> {
}

