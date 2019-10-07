package com.smartsatu.android.paging.example.data

import android.content.Context
import androidx.room.Room
import com.smartsatu.android.paging.example.data.local.UserDatabase
import com.smartsatu.android.paging.example.data.local.model.User
import io.reactivex.Single
import java.util.concurrent.TimeUnit

object UsersRepository {

    private const val PAGES_COUNT = 5
    lateinit var room: UserDatabase

    fun initDb(context: Context) {
        this.room = Room.databaseBuilder(context, UserDatabase::class.java, "users")
                .fallbackToDestructiveMigration()
                .build()
    }

    fun requestUsers(page: Int, pageSize: Int): Single<List<User>> {
        return if (page <= PAGES_COUNT) {
            Single.just(generateUserData(page, pageSize)).doOnSuccess {
                room.userDao().insertAll(it)
            }
        } else {
            Single.just(listOf())
        }.delay(2, TimeUnit.SECONDS)
    }

    private fun generateUserData(page: Int, pageSize: Int) = User.random(page, pageSize)
}