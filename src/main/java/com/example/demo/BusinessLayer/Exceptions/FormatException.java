package com.example.demo.BusinessLayer.Exceptions;

public class FormatException extends Exception {

    public FormatException(String expected,String received) {
        super("Expected: "+expected+"\nreceived: "+received);
    }

    public FormatException(String expected) {
        super("Expected: "+expected);
    }
}
