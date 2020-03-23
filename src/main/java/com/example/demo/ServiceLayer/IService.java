package com.example.demo.ServiceLayer;

import org.json.simple.JSONObject;

public interface IService {
    JSONObject requestProcessor(JSONObject request);
}
