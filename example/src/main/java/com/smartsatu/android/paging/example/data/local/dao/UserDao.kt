package com.smartsatu.android.paging.example.data.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smartsatu.android.paging.example.data.local.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun queryAll(): DataSource.Factory<Int, User>

    @Insert
    fun insertAll(users: List<User>)

    @Query("DELETE FROM users")
    fun deleteAll()
}