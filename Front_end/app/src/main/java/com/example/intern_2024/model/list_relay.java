package com.example.intern_2024.model;

import java.util.HashMap;
import java.util.Map;

public class list_relay {
    private int index;
    private int relay_id;
    private int state;
    private String name;


    public list_relay() {
    }

    public list_relay(int index, int relay_id,int state) {
        String setName = "relay_" + index;
        this.index = index;
        this.relay_id = relay_id;
        this.state = state;
        this.name = setName;
    }

    public list_relay(int index, int relay_id, int state,String name) {
        this.index = index;
        this.relay_id = relay_id;
        this.state = state;
        this.name = name;
    }

    public list_relay(int index, int relay_id) {
        this.index = index;
        this.relay_id = relay_id;
        this.state = 0;
        this.name = name;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRelay_id() {
        return relay_id;
    }

    public void setRelay_id(int relay_id) {
        this.relay_id = relay_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("name", name);
        hashMap.put("state", state);
        hashMap.put("relay_id", relay_id);
        return hashMap;
    }
}
