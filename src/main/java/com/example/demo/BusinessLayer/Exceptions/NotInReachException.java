package com.example.demo.BusinessLayer.Exceptions;

public class NotInReachException extends Exception {
    public NotInReachException(String message) {
        super(message+" is not in your reach");
    }
}
