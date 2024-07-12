package com.example.intern_2024.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private String file;
    private list_relay list_relay;

    public User() {
    }

    public User(com.example.intern_2024.model.list_relay list_relay) {
        this.list_relay = list_relay;
    }

    public User(String name, String email, String file) {
        this.name = name;
        this.email = email;
        this.file = file;
    }

    public User(String name, String email, String file, list_relay list_relay) {
        this.name = name;
        this.email = email;
        this.file = file;
        this.list_relay = list_relay;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public list_relay getListRelay() {
        return list_relay;
    }

    public void setListRelay(list_relay list_relay) {
        this.list_relay = list_relay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("file", file);
        hashMap.put("list_relay", list_relay);
        return hashMap;
    }
}