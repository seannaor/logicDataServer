package com.example.demo.BusinessLayer.Exceptions;

public class NotExistException extends ProjectException {
    public NotExistException(String title, String id) {
        super("couldn't find " + title + " - " + id);
    }
}
