package com.example.intern_2024.notification;

import java.util.ArrayList;
import java.util.List;

public class AlarmList {

    private List<Integer> alarmIndexes;

    // Constructor
    public AlarmList() {
        alarmIndexes = new ArrayList<>();
    }

    public void addAlarmIndex(int index) {
        if (!alarmIndexes.contains(index)) {
            alarmIndexes.add(index);
        }
    }

    public void removeAlarmIndex(int index) {
        alarmIndexes.remove(Integer.valueOf(index));
    }

    public List<Integer> getAlarmIndexes() {
        return new ArrayList<>(alarmIndexes);
    }

    public boolean containsAlarmIndex(int index) {
        return alarmIndexes.contains(index);
    }

    public void clear() {
        alarmIndexes.clear();
    }
}
