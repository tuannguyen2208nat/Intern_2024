package com.example.intern_2024.model;

import java.util.HashMap;
import java.util.Map;

public class list_relay {
    private int index;
    private int relay_id;
    private String name;
    private boolean isChecked;


    public list_relay() {
    }

    public list_relay(int index, int relay_id) {
        String setName = "relay_" + index;
        this.index = index;
        this.relay_id = relay_id;
        this.name = setName;
    }

    public list_relay(int index, int relay_id,String name) {
        this.index = index;
        this.relay_id = relay_id;
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
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }



    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("name", name);
        hashMap.put("relay_id", relay_id);
        return hashMap;
    }
}
