package com.smartsatu.android.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.smartsatu.android.live.NetworkState

data class Paging<T>(
        val pagedList: LiveData<PagedList<T>>,
        val networkState: LiveData<NetworkState>,
        val refreshState: LiveData<NetworkState>,
        val refresh: () -> Unit,
        val retry: () -> Unit,
        val shutdown: () -> Unit
)