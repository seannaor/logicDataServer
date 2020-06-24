package com.example.demo.BusinessLayer.Exceptions;

public class ExpEndException extends ProjectException {
    public ExpEndException() {
        super("You have finished the experiment already");
    }
}
