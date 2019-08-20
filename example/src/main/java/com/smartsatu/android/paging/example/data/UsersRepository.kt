package com.smartsatu.android.paging.example.data

import android.content.Context
import androidx.room.Room
import com.smartsatu.android.paging.example.data.local.UserDatabase
import com.smartsatu.android.paging.example.data.local.model.User
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object UsersRepository {

    private const val PAGES_COUNT = 5
    lateinit var room: UserDatabase

    fun initDb(context: Context) {
        this.room = Room.databaseBuilder(context, UserDatabase::class.java, "users")
                .fallbackToDestructiveMigration()
                .build()
    }

    fun requestUsers(page: Int, pageSize: Int): Observable<List<User>> {
        return if (page <= PAGES_COUNT) {
            Observable.just(generateUserData(page, pageSize)).doOnNext {
                UsersRepository.room.userDao().insertAll(it)
            }
        } else {
            Observable.just(listOf())
        }.delay(2, TimeUnit.SECONDS)
    }

    private fun generateUserData(page: Int, pageSize: Int) = User.random(page, pageSize)
}