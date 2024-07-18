package com.example.intern_2024.fragment.Automation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<list_relay>> relay = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<list_auto> auto = new MutableLiveData<>();

    public void SetListRelay(ArrayList<list_relay> listRelay) {
        relay.setValue(listRelay);
    }

    public LiveData<ArrayList<list_relay>> GetListRelay() {
        return relay;
    }

    public void SetListAuto(list_auto listAuto) {
        auto.setValue(listAuto);
    }

    public LiveData<list_auto> GetListAuto() {
        return auto;
    }
}