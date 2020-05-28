package com.example.demo.BusinessLayer.Exceptions;

public class NotInReachException extends Exception {
    public NotInReachException(String something) {
        super(something+" is not in your reach");
    }

    public NotInReachException(String something, String because) {
        super(something+" is not in your reach because "+because);
    }
}
