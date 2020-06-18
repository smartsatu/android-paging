package com.smartsatu.android.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.smartsatu.android.live.NetworkState
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.ExecutorService

internal class PageBoundaryCallBack<Param : PagingParams, Item>(
        private val ioExecutor: ExecutorService,
        private val requestHandler: (params: Param) -> Single<List<Item>>,
        private val itemLoadedCount: () -> Int
) : PagedList.BoundaryCallback<Item>() {

    private var lastPageWasNotFull = false
    lateinit var params: Param
    private var subscriptions = CompositeDisposable()
    private val helper = PagingRequestHelper(ioExecutor)
    val networkState = MutableLiveData<NetworkState>()
    val initialLoadState = MutableLiveData<NetworkState>()
    private var awaitForMoreItems = true
    @Volatile
    var isShuttingDown = false

    override fun onZeroItemsLoaded() {
        Timber.d("isShuttingDown: $isShuttingDown")
        if (isShuttingDown) {
            isShuttingDown = false
            return
        }
        lastPageWasNotFull = false
        awaitForMoreItems = false
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            initialLoadState.postValue(NetworkState.LOADING)
            params.page = 1
            val pagingRequestCallBack = it
            val itemsSingle = requestHandler(params)
            val subscription = Single.zip(itemsSingle, Single.just(it),
                    BiFunction<List<Item>, PagingRequestHelper.Request.Callback, PageResponse<Item>> { items, callback -> PageResponse(items, callback) })
                    .subscribeOn(Schedulers.from(ioExecutor))
                    .doOnDispose {
                        Timber.d("Dispose: ${pagingRequestCallBack.requestType.name}")
                        pagingRequestCallBack.recordCanceled()
                        initialLoadState.postValue(NetworkState.EMPTY)
                    }
                    .doOnSuccess { lastPageWasNotFull = it.items.size < params.pageSize }
                    .subscribe({ handleSuccess(it) }, { handleError(it, pagingRequestCallBack) })
            subscriptions.add(subscription)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Item) {
        // Dynamically calculate from what page we should load
        if (awaitForMoreItems) {
            // TODO: Fix me urgently cause it's not really right way to check fr
            params.page = itemLoadedCount.invoke() / params.pageSize
            awaitForMoreItems = false
        }
        if (lastPageWasNotFull) {
            return
        }
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            networkState.postValue(NetworkState.LOADING)
            // It's really important to know how many items have been preloaded from sqlite
            // In case we have less than entire page items loaded then we have to load a very first page
            // Or we must be sure that no items are preloaded for just selected page (specific category id or something similar)
            params.nextPage()
            val pagingRequestCallBack = it
            val itemsSingle = requestHandler(params)
            val subscription = Single.zip(itemsSingle, Single.just(it),
                    BiFunction<List<Item>, PagingRequestHelper.Request.Callback, PageResponse<Item>> { items, callback -> PageResponse(items, callback) })
                    .subscribeOn(Schedulers.from(ioExecutor))
                    .doOnDispose {
                        Timber.d("Dispose: ${pagingRequestCallBack.requestType.name}")
                        pagingRequestCallBack.recordCanceled()
                        networkState.postValue(NetworkState.EMPTY)
                    }
                    .doOnSuccess { lastPageWasNotFull = it.items.size < params.pageSize }
                    .subscribe({ handleSuccess(it) }, { handleError(it, pagingRequestCallBack) })
            subscriptions.add(subscription)
        }
    }

    private fun handleSuccess(pageResponse: PageResponse<Item>) {
        Timber.d("Success: ${pageResponse.callback.requestType.name}")
        if (params.page == 1 && pageResponse.items.isEmpty()) {
            if (pageResponse.callback.requestType == PagingRequestHelper.RequestType.INITIAL) {
                initialLoadState.postValue(NetworkState.EMPTY)
            } else {
                networkState.postValue(NetworkState.EMPTY)
            }
        } else {
            if (pageResponse.callback.requestType == PagingRequestHelper.RequestType.INITIAL) {
                initialLoadState.postValue(NetworkState.LOADED)
            } else {
                networkState.postValue(NetworkState.LOADED)
            }
        }
        pageResponse.callback.recordSuccess()
    }

    private fun handleError(throwable: Throwable, callback: PagingRequestHelper.Request.Callback) {
        Timber.d("Error: ${callback.requestType.name}")
        if (callback.requestType == PagingRequestHelper.RequestType.INITIAL) {
            initialLoadState.postValue(NetworkState.error(throwable.message, throwable))
        } else {
            networkState.postValue(NetworkState.error(throwable.message, throwable))
        }
        callback.recordFailure(throwable)
    }

    /**
     * Dispose all postponed requests and control if empty state should be propagated to UI
     *
     * @param skipEmptyState means that a new request will be made immediately after shutdown
     */
    fun shutdown(skipEmptyState: Boolean) {
        isShuttingDown = true
        subscriptions.clear()
        if (!skipEmptyState) {
            initialLoadState.postValue(NetworkState.EMPTY)
            networkState.postValue(NetworkState.EMPTY)
        }
    }

    data class PageResponse<Item>(val items: List<Item>, val callback: PagingRequestHelper.Request.Callback)
}