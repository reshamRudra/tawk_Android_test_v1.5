package com.rudra.tawkto

import android.app.Application
import androidx.room.Room
import com.rudra.tawkto.room.AppDatabase

class UserMasterApplication : Application() {

    companion object {
        var database: AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        UserMasterApplication.database = Room.databaseBuilder(
            this, AppDatabase::class.java,
            "user-master-db"
        ).build()
    }
}