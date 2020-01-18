package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	StageRep rep;

	@Autowired
	GradingTaskRep gRep;

	@Test
	void contextLoads() {
		Experiment experiment = new Experiment("hello");
		Stage s = new Stage(1,1);
		rep.save(s);
		System.out.println("OK");
	}

}
