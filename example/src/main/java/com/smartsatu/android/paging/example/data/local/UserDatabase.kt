package com.smartsatu.android.paging.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartsatu.android.paging.example.data.local.dao.UserDao
import com.smartsatu.android.paging.example.data.local.model.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}