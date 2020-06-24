package com.example.demo.BusinessLayer.Exceptions;

public class CodeException extends ProjectException {

    public CodeException(String code) {
        super("code " + code + " is not valid");
    }
}
