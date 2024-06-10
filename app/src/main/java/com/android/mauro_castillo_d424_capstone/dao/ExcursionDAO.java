package com.android.mauro_castillo_d424_capstone.dao;

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
    void delete(Excursion delete);

    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionId ASC")
    List<Excursion> getAllExcursions();

    @Query("SELECT * FROM excursions WHERE vacationId=:vac ORDER BY excursionId ASC")
    List<Excursion> getAssociatedExcursions(int vac);
}
