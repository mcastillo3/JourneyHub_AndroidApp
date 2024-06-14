package com.android.mauro_castillo_d424_capstone.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ExcursionViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;
    private final int vacationID;

    public ExcursionViewModelFactory(@NonNull Application application, int vacationID) {
        this.application = application;
        this.vacationID = vacationID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Application.class, int.class).newInstance(application, vacationID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of" + modelClass, e);
        }
    }
}
