package com.smartsatu.android.paging

import androidx.annotation.MainThread
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import java.util.concurrent.Executor

/**
 * Repository implementation that returns a Listing that loads data directly from network by using
 * the previous / next page keys returned in the query.
 */
@Deprecated("Use PagingRoomRepository instead")
abstract class PagingRepository<Param : PagingParams, Item> : PaginationRepository<Param, Item> {

    abstract fun provideNetworkExecutor(): Executor
    abstract fun provideConcreteDataSource(params: Param): PagingDataSource<Param, Item>

    private val networkExecutor: Executor by lazy {
        provideNetworkExecutor()
    }

    @MainThread
    override fun fetchPaging(params: Param): Paging<Item> {
        val sourceFactory = object : PagingDataSourceFactory<Param, Item, PagingDataSource<Param, Item>>(params) {
            override fun createConcreteDataSource(params: Param): PagingDataSource<Param, Item> {
                return provideConcreteDataSource(params)
            }
        }

        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(params.pageSize / 2)
                .setPageSize(params.pageSize)
                .build()

        val livePagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                .setFetchExecutor(networkExecutor)
                .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Paging(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData, {
                    it.networkState
                }),
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState,
                shutdown = { }
        )
    }
}

