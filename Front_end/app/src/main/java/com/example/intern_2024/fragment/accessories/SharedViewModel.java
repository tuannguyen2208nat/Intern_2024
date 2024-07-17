package com.example.intern_2024.fragment.accessories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.intern_2024.model.list_auto;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String > auto = new MutableLiveData<>();

    public void setEditTextValue(String value) {
        auto.setValue(value);
    }

    public LiveData<String> getEditTextValue() {
        return auto;
    }
}