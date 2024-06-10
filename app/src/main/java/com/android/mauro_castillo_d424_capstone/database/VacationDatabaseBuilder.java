package com.android.mauro_castillo_d424_capstone.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.mauro_castillo_d424_capstone.dao.ExcursionDAO;
import com.android.mauro_castillo_d424_capstone.dao.VacationDAO;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 6, exportSchema = false)
public abstract class VacationDatabaseBuilder extends RoomDatabase {

    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    private static volatile VacationDatabaseBuilder INSTANCE;

    static VacationDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VacationDatabaseBuilder.class, "MyVacationDatabase.db")
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
