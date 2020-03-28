package com.example.demo.ServiceLayer;

import java.util.Map;

public interface IService {
    Map<String,Object> requestProcessor(Map<String,Object> request);
}
