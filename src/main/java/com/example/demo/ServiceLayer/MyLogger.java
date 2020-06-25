package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Exceptions.ProjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.*;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class MyFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        String stringRecord = record.getThreadID()+" :: "+record.getSourceClassName()+" :: "
                +record.getSourceMethodName()+" :: "
                +new Date(record.getMillis())+" :: "+
                "["+record.getLevel().getName()+"] "+
                record.getMessage()+"\n";
        return stringRecord;
    }

}

public class MyLogger {

    private static final Logger LOGGER = Logger.getLogger("Experiential system logger");
    private static MyLogger logger=null;


    private MyLogger() {
        LOGGER.setLevel(Level.ALL);
        // Creating FileHandler
        Formatter simpleFormatter = new MyFormatter();


        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("./Log.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(simpleFormatter);

        // Assigning handler to logger
        LOGGER.addHandler(fileHandler);

        LOGGER.info("Logger action has started!");
    }

    public static void init(){
        if(logger==null) logger = new MyLogger();
    }

    public static void log(Exception e){
        init();
        if(e instanceof ProjectException)
            LOGGER.log(Level.WARNING,e +"\n"+Arrays.toString(e.getStackTrace()));
        else LOGGER.log(Level.SEVERE,e +"\n"+Arrays.toString(e.getStackTrace()));
    }
}