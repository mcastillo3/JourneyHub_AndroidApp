package com.android.mauro_castillo_d424_capstone.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.mauro_castillo_d424_capstone.entities.User;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Query("SELECT * FROM users WHERE userName=:username LIMIT 1")
    User getUserByUserName(String username);
}
