package com.nieldeokar.hurumessenger.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nieldeokar.hurumessenger.database.entity.AccountEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by @nieldeokar on 13/06/18.
 */

@Dao
public interface AccountDao {

    @Query("SELECT * FROM account")
    List<AccountEntity> getAll();

    @Query("SELECT * FROM account where aid = :id")
    AccountEntity findByAid(int id);

    @Insert(onConflict = REPLACE)
    void insertAccount(AccountEntity accountEntity);

    @Delete
    void delete(AccountEntity accountEntity);

    @Update(onConflict = REPLACE)
    public void updateAccount(AccountEntity accountEntity);
}