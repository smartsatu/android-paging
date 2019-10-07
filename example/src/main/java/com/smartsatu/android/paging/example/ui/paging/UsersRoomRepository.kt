package com.smartsatu.android.paging.example.ui.paging

import androidx.paging.DataSource
import com.smartsatu.android.paging.PagingRoomRepository
import com.smartsatu.android.paging.example.data.UsersRepository
import com.smartsatu.android.paging.example.data.local.model.User
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class UsersRoomRepository(private val usersRepository: UsersRepository) : PagingRoomRepository<UserParams, User>() {
    override fun clearRoom() {
        // Legacy
    }

    override fun clearRoom(params: UserParams) {
        usersRepository.room.userDao().deleteAll()
    }

    override fun provideNetworkExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    override fun provideDataSourceFactory(params: UserParams): DataSource.Factory<Int, User> {
        return usersRepository.room.userDao().queryAll()
    }

    override fun requestAndStoreData(params: UserParams): Single<List<User>> {
        return usersRepository.requestUsers(params.page, params.pageSize)
    }
}