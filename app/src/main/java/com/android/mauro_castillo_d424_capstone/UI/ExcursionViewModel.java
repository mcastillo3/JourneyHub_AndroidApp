package com.android.mauro_castillo_d424_capstone.UI;

import android.app.Application;

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
    private MediatorLiveData<List<Excursion>> searchResults = new MediatorLiveData<List<Excursion>>();
    private LiveData<List<Excursion>> currentSource;
    Vacation currentVacation;

    public ExcursionViewModel(@NonNull Application application) throws InterruptedException {
        super(application);
        mRepository = new Repository(application);

        //List<Excursion> filteredExcursions = new ArrayList<>();
        try {
            for (Excursion e : mRepository.getmAllExcursions()) {
                if (e.getVacationId() == currentVacation.getVacationId()) {
                    //filteredExcursions.add(e);
                    insert(e);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // display current excursions from database
        LiveData<List<Excursion>> initialData = mRepository.getAllExcursions();
        searchResults.addSource(initialData, searchResults::setValue);
        currentSource = initialData;

        // search for excursions in database
        searchQuery.observeForever(query -> {
            LiveData<List<Excursion>> newSource;
            if (query == null || query.isEmpty()) {
                try {
                    newSource = mRepository.getAllExcursions();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                newSource = mRepository.searchEDatabase(query);
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

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insert(Excursion excursion) throws InterruptedException {
        mRepository.insert(excursion);
    }
}






























