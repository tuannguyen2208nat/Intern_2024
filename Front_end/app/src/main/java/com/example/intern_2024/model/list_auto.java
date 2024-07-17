package com.example.intern_2024.model;

import java.util.HashMap;
import java.util.Map;

public class list_auto {
    private int index;
    private String name;
    private list_relay list_relay;

    public list_auto() {
    }

    public list_auto(int index, com.example.intern_2024.model.list_relay list_relay) {
        this.index = index;
        this.list_relay = list_relay;
    }

    public list_auto(int index, String name, com.example.intern_2024.model.list_relay list_relay) {
        this.index = index;
        this.name = name;
        this.list_relay = list_relay;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.example.intern_2024.model.list_relay getList_relay() {
        return list_relay;
    }

    public void setList_relay(com.example.intern_2024.model.list_relay list_relay) {
        this.list_relay = list_relay;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("name", name);
        hashMap.put("list_relay", list_relay);
        return hashMap;
    }
}
