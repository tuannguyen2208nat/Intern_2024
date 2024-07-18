package com.example.intern_2024.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class list_auto {
    private int index;
    private String name;
    private List<list_relay> listRelays;

    public list_auto() {
        this.listRelays = new ArrayList<>();
    }

    public list_auto(int index, List<list_relay> listRelays) {
        this.index = index;
        this.listRelays = listRelays;
    }

    public list_auto(int index, String name, List<list_relay> listRelays) {
        this.index = index;
        this.name = name;
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

    public void waybackvalue() {
        for (list_relay relay : listRelays) {
            relay.setChecked(false);
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("name", name);
        hashMap.put("listRelays", listRelays);
        return hashMap;
    }
}
