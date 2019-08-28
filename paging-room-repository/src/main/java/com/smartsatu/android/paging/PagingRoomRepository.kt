package com.smartsatu.android.paging

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.smartsatu.android.live.NetworkState
import io.reactivex.Single
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class PagingRoomRepository<Param : PagingParams, Item>(private val isLocalOnly: Boolean = false) : PaginationRepository<Param, Item> {

    private val boundaryCallback: PageBoundaryCallBack<Param, Item> by lazy {
        PageBoundaryCallBack(Executors.newSingleThreadExecutor(), this::requestAndStoreData, this::itemLoadedCount)
    }

    private val pagedListConfig by lazy {
        PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(6)
                .setInitialLoadSizeHint(400)
                .setPageSize(20)
                .build()
    }

    private lateinit var livePagedList: LiveData<PagedList<Item>>

    @Deprecated("Method will never be called. Override fun clearRoom(params: Param) instead")
    abstract fun clearRoom()

    open fun clearRoom(params: Param) = Unit

    abstract fun provideNetworkExecutor(): Executor

    abstract fun provideDataSourceFactory(params: Param): DataSource.Factory<Int, Item>

    abstract fun requestAndStoreData(params: Param): Single<List<Item>>

    private fun refresh() {
        boundaryCallback.onZeroItemsLoaded()
    }

    @MainThread
    override fun fetchPaging(params: Param): Paging<Item> {
        boundaryCallback.params = params

        val dataSourceFactory = provideDataSourceFactory(params)

        livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                .setFetchExecutor(provideNetworkExecutor())
                .apply { if (!isLocalOnly) this.setBoundaryCallback(boundaryCallback) }
                .build()

        return Paging(
                pagedList = livePagedList,
                networkState = boundaryCallback.networkState,
                retry = {
                    // TODO: Retry all latest failed results
                },
                refresh = {
                    //livePagedList.value?.dataSource?.invalidate()
                    Executors.newSingleThreadExecutor().execute {
                        clearRoom(params)
                        refresh()
                    }
                },
                refreshState = boundaryCallback.initialLoadState,
                shutdown = {
                    boundaryCallback.isShuttingDown = true
                    clearRoom(params)
                    boundaryCallback.networkState.postValue(NetworkState.EMPTY)
                })
    }

    private fun itemLoadedCount(): Int {
        return livePagedList.value?.size ?: 0
    }
}