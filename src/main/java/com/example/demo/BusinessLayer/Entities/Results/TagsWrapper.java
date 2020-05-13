package com.example.demo.BusinessLayer.Entities.Results;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TagsWrapper implements ResultWrapper {

    List<RequirementTag> tags = new ArrayList<>();

    public void addTag(RequirementTag tag){
        if(!tags.contains(tag))
            tags.add(tag);
    }

    public List<RequirementTag> getTags() {
        return tags;
    }

    @Override
    public JSONObject getAsJson() {
        JSONObject json = new JSONObject();
        json.put("source stage","tagging");
        return json;
    }
}
