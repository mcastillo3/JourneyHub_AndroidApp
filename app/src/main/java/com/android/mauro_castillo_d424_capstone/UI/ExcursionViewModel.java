package com.android.mauro_castillo_d424_capstone.UI;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class ExcursionViewModel extends AndroidViewModel {

    private Repository mRepository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MediatorLiveData<List<Excursion>> searchResults = new MediatorLiveData<>();
    private LiveData<List<Excursion>> currentSource;

    public ExcursionViewModel(@NonNull Application application, int vacationID) {
        super(application);
        mRepository = new Repository(application);

        // display current excursions from database
        LiveData<List<Excursion>> initialData = mRepository.getAssociatedExcursions(vacationID);
        searchResults.addSource(initialData, searchResults::setValue);
        currentSource = initialData;

        // search for excursions in database
        searchQuery.observeForever(query -> {
            LiveData<List<Excursion>> newSource;
            if (query == null || query.isEmpty()) {
                newSource = mRepository.getAssociatedExcursions(vacationID);
            } else {
                newSource = mRepository.searchEDatabase(vacationID, query);

            }
            updateSource(newSource);
        });
    }

    private void updateSource(LiveData<List<Excursion>> newSource) {

        // remove current observable source if it exists
        if (currentSource != null) {
            searchResults.removeSource(currentSource);
        }

        // add new observable source
        currentSource = newSource;
        searchResults.addSource(currentSource, searchResults::setValue);
    }

    public LiveData<List<Excursion>> getAllExcursions() {
        return  searchResults;
    }

    public LiveData<List<Excursion>> getAssociatedExcursions(int vacationID) {
        return mRepository.getAssociatedExcursions(vacationID);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insert(Excursion excursion) throws InterruptedException {
        mRepository.insert(excursion);
    }
}






























