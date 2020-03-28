package com.example.demo.RoutingLayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringBooter extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SpringBooter.class, args);
    }
}
