package com.android.mauro_castillo_d424_capstone.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.android.mauro_castillo_d424_capstone.dao.ExcursionDAO;
import com.android.mauro_castillo_d424_capstone.dao.UserDAO;
import com.android.mauro_castillo_d424_capstone.dao.VacationDAO;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;
import com.android.mauro_castillo_d424_capstone.entities.User;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private ExcursionDAO mExcursionDAO;
    private VacationDAO mVacationDAO;
    private UserDAO mUserDAO;
    private List<Vacation> mAllVacations;
    private List<Excursion> mAllExcursions;
    private LiveData<List<Vacation>> allVacations;
    private LiveData<List<Excursion>> allExcursions;

    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService DATABASE_EXECUTOR = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mVacationDAO = db.vacationDAO();
        mUserDAO = db.userDAO();
        allVacations = mVacationDAO.readData();
        allExcursions = mExcursionDAO.readData();
    }

    public void insert(User user) throws InterruptedException {
        DATABASE_EXECUTOR.execute(()-> {
            mUserDAO.insert(user);
        });
        Thread.sleep(1000);
    }

    public void getUserByUserName(String username, RepositoryCallback<User> callBack) {
        DATABASE_EXECUTOR.execute(() -> {
            User user = mUserDAO.getUserByUserName(username);
            callBack.onComplete(user);
        });
    }

    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }

    public List<Vacation> getmAllVacations() throws InterruptedException {
        DATABASE_EXECUTOR.execute(() -> {
            mAllVacations = mVacationDAO.getAllVacations();
        });
        Thread.sleep(1000);
        return mAllVacations;
    }

    public void insert(Vacation vacation) throws InterruptedException {
        DATABASE_EXECUTOR.execute(()-> {
            mVacationDAO.insert(vacation);
        });
        Thread.sleep(1000);
    }

    public void update(Vacation vacation) throws InterruptedException {
        DATABASE_EXECUTOR.execute(()-> {
            mVacationDAO.update(vacation);
        });
        Thread.sleep(1000);
    }

    public void delete(Vacation vacation) throws InterruptedException {
        DATABASE_EXECUTOR.execute(()-> {
            mVacationDAO.delete(vacation);
        });
        Thread.sleep(1000);
    }

    public List<Vacation> getmAssociatedExcursions(int vacationId) throws InterruptedException {
        DATABASE_EXECUTOR.execute(() -> {
            mAllExcursions = mExcursionDAO.getmAssociatedExcursions(vacationId);
        });
        Thread.sleep(1000);
        return mAllVacations;
    }

    public List<Excursion> getmAllExcursions() throws InterruptedException {
        DATABASE_EXECUTOR.execute(()->{
            mAllExcursions = mExcursionDAO.getAllExcursions();
        });
        Thread.sleep(1000);
        return mAllExcursions;
    }

    public void insert(Excursion excursion) throws InterruptedException {
        DATABASE_EXECUTOR.execute(()-> {
            mExcursionDAO.insert(excursion);
        });
        Thread.sleep(1000);
    }

    public void update(Excursion excursion) throws InterruptedException {
        DATABASE_EXECUTOR.execute(()-> {
            mExcursionDAO.update(excursion);
        });
        Thread.sleep(1000);
    }

    public void delete(Excursion excursion) throws InterruptedException {
        DATABASE_EXECUTOR.execute(() -> {
            mExcursionDAO.delete(excursion);
        });
        Thread.sleep(1000);
    }

    public LiveData<List<Vacation>> getAllVacations() throws InterruptedException {
        return allVacations;
    }

    public LiveData<List<Vacation>> searchVDatabase(String searchQuery) {
        return mVacationDAO.searchDatabase("%" + searchQuery + "%");
    }

    public LiveData<List<Excursion>> searchEDatabase(int vacationID, String searchQuery) {
        return mExcursionDAO.searchDatabase(vacationID,"%" + searchQuery + "%");
    }

    public LiveData<List<Excursion>> getAssociatedExcursions(int vacationId) {
        return mExcursionDAO.getAssociatedExcursions(vacationId);
    }

}