package com.example.demo.BusinessLayer.Exceptions;

public class ExistException extends Exception {
    public ExistException(String name) {
        super(name + " already exist");
    }

    public ExistException(String name, String where) {
        super(name + " already exist in " + where);
    }


}
