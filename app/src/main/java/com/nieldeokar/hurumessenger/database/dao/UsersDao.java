package com.nieldeokar.hurumessenger.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.nieldeokar.hurumessenger.database.entity.User;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by @nieldeokar on 13/06/18.
 */

@Dao
public interface UsersDao {

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users where uid = :id")
    User findByUid(int id);

    @Query("SELECT * FROM users where device_id = :deviceId")
    User findByDeviceId(String deviceId);

    @Insert(onConflict = IGNORE)
    void insertAll(List<User> usersEntities);

    @Insert(onConflict = REPLACE)
    void insertUser(User userEntity);

    @Delete
    void delete(User userEntity);

    @Update(onConflict = REPLACE)
    public void updateUser(User userEntity);
}