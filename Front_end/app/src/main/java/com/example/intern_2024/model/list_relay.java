package com.example.intern_2024.model;

import java.util.HashMap;
import java.util.Map;

public class list_relay {
    private int index;
    private int value;
    private String name;

    public list_relay() {
    }

    public list_relay(int index, int value) {
        String setName = "relay_" + index;
        this.index = index;
        this.value = value;
        this.name = setName;
    }

    public list_relay(int index, int value, String name) {
        this.index = index;
        this.value = value;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("name", name);
        hashMap.put("value", value);
        return hashMap;
    }
}
