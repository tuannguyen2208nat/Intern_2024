package com.example.intern_2024.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class list_auto implements Serializable {
    private int index;
    private String name;
    private int mode;
    private String time;
    private List<list_relay> listRelays;

    public list_auto() {

    }

    public list_auto(int index,int mode,String time , List<list_relay> listRelays) {
        this.index = index;
        this.mode=mode;
        this.time=time;
        this.listRelays = listRelays;
    }

    public list_auto(int index,int mode, String time,String name, List<list_relay> listRelays) {
        this.index = index;
        this.name = name;
        this.mode=mode;
        this.time=time;
        this.listRelays = listRelays;
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

    public List<list_relay> getListRelays() {
        return listRelays;
    }

    public void setListRelays(List<list_relay> listRelays) {
        this.listRelays = listRelays;
    }

    public int getSize() {
        return listRelays.size();
    }

    public void addRelay(list_relay relay) {
        this.listRelays.add(relay);
    }

    public void removeRelay(list_relay relay) {
        this.listRelays.remove(relay);
    }

    public int getMode() {return mode;}

    public void setMode(int mode) {this.mode = mode;}

    public String getTime() {return time;}

    public void setTime(String time) { this.time = time; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("name", name);
        hashMap.put("listRelays", listRelays);
        return hashMap;
    }
}