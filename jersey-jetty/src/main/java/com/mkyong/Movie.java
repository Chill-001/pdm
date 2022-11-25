package com.mkyong;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {

    String name;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        try {
            // takes advantage of toString() implementation to format {"a":"b"}
            return new JSONObject().put("id", id).put("name", name).toString();
        } catch (JSONException e) {
            return null;
        }
    }
}
