package com.nileshdeokar.healthapp.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.LiveData
import com.nieldeokar.hurumessenger.database.dao.AccountDao
import com.nieldeokar.hurumessenger.database.dao.UsersDao
import com.nieldeokar.hurumessenger.database.entity.AccountEntity
import com.nieldeokar.hurumessenger.database.entity.UserEntity

/**
 * Created by @nieldeokar on 27/05/18.
 */

@Database(entities = [UserEntity::class,AccountEntity::class], version = 1)

abstract class AppDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao

    abstract fun accountDao(): AccountDao

    companion object {

        private var sInstance: AppDatabase? = null

        private var DATABASE_NAME = "hulu-db"

        fun destroyInstance() {
            sInstance = null
        }

        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = buildDatabase(context.applicationContext)
                    }
                }
            }
            return sInstance
        }

        private fun buildDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                    .build()
        }

    }

}