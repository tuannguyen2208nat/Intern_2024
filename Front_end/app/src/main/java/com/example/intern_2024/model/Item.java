package com.example.intern_2024.model;

import java.io.Serializable;
public class Item implements Serializable {
    private int id;
    private final String time;
    private final String detail;


    public Item(int id, String time, String detail) {
        this.id = id;
        this.time = time;
        this.detail = detail;
    }

    public Item(String time, String detail) {
        this.time = time;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public String getDetail() {
        return detail;
    }

    public String getTime() {
        return time;
    }
}
