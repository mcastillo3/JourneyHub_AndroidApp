package com.android.mauro_castillo_d424_capstone.UI;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;

import java.util.List;

public class VacationViewModel extends AndroidViewModel {

    private Repository mRepository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MediatorLiveData<List<Vacation>> searchResults = new MediatorLiveData<>();
    private LiveData<List<Vacation>> currentSource;

    public VacationViewModel(@NonNull Application application) throws InterruptedException {
        super(application);
        mRepository = new Repository(application);

        // display current vacations in database
        LiveData<List<Vacation>> initialData = mRepository.getAllVacations();
        searchResults.addSource(initialData, searchResults::setValue);
        currentSource = initialData;

        // search for vacations in database
        searchQuery.observeForever(query -> {
            LiveData<List<Vacation>> newSource;
            if (query == null || query.isEmpty()) {
                try {
                    newSource = mRepository.getAllVacations();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                newSource = mRepository.searchDatabase(query);
            }
            updateSource(newSource);
        });
    }

    private void updateSource(LiveData<List<Vacation>> newSource) {

        // remove current observable source if it exists
        if (currentSource != null) {
            searchResults.removeSource(currentSource);
        }

        // add new observable source
        currentSource = newSource;
        searchResults.addSource(currentSource, searchResults::setValue);
    }

    public LiveData<List<Vacation>> getAllVacations() {
        return searchResults;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insert(Vacation vacation) throws InterruptedException {
        mRepository.insert(vacation);
    }
}