package com.example.demo;

import com.example.demo.ServiceLayer.MyLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringBooter extends SpringBootServletInitializer {
    public static void main(String[] args) {
        MyLogger.init();
        SpringApplication.run(SpringBooter.class, args);
    }
}