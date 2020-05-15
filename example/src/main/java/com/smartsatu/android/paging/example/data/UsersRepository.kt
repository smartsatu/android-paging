package com.smartsatu.android.paging.example.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import com.smartsatu.android.paging.example.data.local.UserDatabase
import com.smartsatu.android.paging.example.data.local.model.User
import io.reactivex.Single
import timber.log.Timber

object UsersRepository {

    private val pagesCountMutable = MutableLiveData<Int>().apply { value = 0 }
    val pagesCount = Transformations.map(pagesCountMutable) { it }

    lateinit var room: UserDatabase

    fun initDb(context: Context) {
        this.room = Room.databaseBuilder(context, UserDatabase::class.java, "users")
                .fallbackToDestructiveMigration()
                .build()
    }

    fun addPage() {
        pagesCountMutable.value = pagesCountMutable.value?.plus(1)
    }

    fun empty() {
        pagesCountMutable.value = 0
    }

    fun requestUsers(page: Int, pageSize: Int): Single<List<User>> {
        return if (page <= pagesCountMutable.value ?: 0) {
            Single.fromCallable {
                try {
                    Thread.sleep(2000)
                } catch (_: Exception) {
                    Timber.d("Request has been interrupted")
                }
                generateUserData(page, pageSize)
            }.doOnSuccess { room.userDao().insertAll(it) }
        } else {
            Single.just(listOf())
        }
    }

    private fun generateUserData(page: Int, pageSize: Int) = User.random(page, pageSize)
}