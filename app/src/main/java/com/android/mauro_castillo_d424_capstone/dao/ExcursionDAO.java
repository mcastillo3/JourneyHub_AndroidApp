package com.android.mauro_castillo_d424_capstone.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.android.mauro_castillo_d424_capstone.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionId ASC")
    List<Excursion> getAllExcursions();

    @Query("SELECT * FROM excursions WHERE vacationId=:vac ORDER BY excursionId ASC")
    List<Excursion> getmAssociatedExcursions(int vac);

    @Query("SELECT * FROM excursions ORDER BY excursionId ASC")
    LiveData<List<Excursion>> readData();

    @Query("SELECT * FROM excursions WHERE vacationId=:vac AND excursionName LIKE :searchQuery ORDER BY excursionId ASC")
    LiveData<List<Excursion>> searchDatabase(int vac, String searchQuery);

    @Query("SELECT * FROM excursions WHERE vacationId=:vac ORDER BY excursionId ASC")
    LiveData<List<Excursion>> getAssociatedExcursions(int vac);
}
