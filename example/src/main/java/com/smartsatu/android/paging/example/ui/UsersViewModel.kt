package com.smartsatu.android.paging.example.ui

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.Transformations
import com.smartsatu.android.live.LiveState
import com.smartsatu.android.live.LiveStateType
import com.smartsatu.android.live.LiveViewModel
import com.smartsatu.android.live.NetworkState
import com.smartsatu.android.live.toLiveState
import com.smartsatu.android.paging.example.data.UsersRepository
import com.smartsatu.android.paging.example.ui.paging.UserParams
import com.smartsatu.android.paging.example.ui.paging.UsersRoomRepository

class UsersViewModel(application: Application) : LiveViewModel(application) {

    private val usersRoomRepository = UsersRoomRepository(UsersRepository)

    private var params = MutableLiveData<UserParams>()

    private val usersPaging = Transformations.map(params) { usersRoomRepository.fetchPaging(it) }

    val users = Transformations.switchMap(usersPaging) { it.pagedList }

    val networkState = Transformations.switchMap(usersPaging) {
        MediatorLiveData<NetworkState>().apply {
            addSource(it.networkState) {
                value = it
                syncPageLiveState(networkState = it)
            }
            addSource(it.refreshState) {
                syncPageLiveState(networkState = it)
                isRefreshing.value = it == NetworkState.LOADING
            }
        }
    }

    private fun syncPageLiveState(networkState: NetworkState) {
        liveState.postValue(networkState.toLiveState(
                emptyState = LiveState(LiveStateType.EMPTY, title = "Empty"),
                errorState = LiveState(LiveStateType.ERROR, title = "Error")))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        params.value = UserParams()
    }

    fun refresh() {
        usersPaging.value?.refresh?.invoke()
    }

    fun cancel() {
        usersPaging.value?.shutdown?.invoke() {}
    }
}