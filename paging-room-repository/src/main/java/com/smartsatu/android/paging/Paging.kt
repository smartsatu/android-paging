package com.smartsatu.android.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.smartsatu.android.live.NetworkState
import io.reactivex.Completable

typealias ShutdownCallbackStub = () -> Unit
typealias ShutdownCompletable = (skipEmptyState: Boolean) -> Completable

data class Paging<T>(
        val pagedList: LiveData<PagedList<T>>,
        val networkState: LiveData<NetworkState>,
        val refreshState: LiveData<NetworkState>,
        val refresh: () -> Unit,
        val retry: () -> Unit,
        val shutdown: (skipEmptyState: Boolean, callback: ShutdownCallbackStub) -> Unit,
        val shutdownCompletable: ShutdownCompletable? = null
)