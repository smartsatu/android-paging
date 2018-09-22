package com.smartsatu.android.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.smartsatu.android.live.NetworkState
import com.smartsatu.android.paging.util.uiSubscribe
import io.reactivex.Observable
import java.io.IOException
import java.util.concurrent.Executor

@Deprecated("Use PagingRoomRepository instead")
abstract class PagingDataSource<Param : PagingParams, Item>(private val params: Param) : ItemKeyedDataSource<Param, Item>() {

    abstract fun provideRetryExecutor(): Executor
    abstract fun provideObservable(params: Param?): Observable<List<Item>>

    private val retryExecutor: Executor by lazy {
        provideRetryExecutor()
    }

    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()
    var loadedInitialSize = 0

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(params: LoadParams<Param>, callback: LoadCallback<Item>) {
        callback.onResult(listOf())
    }

    override fun loadAfter(params: LoadParams<Param>, callback: LoadCallback<Item>) {
        if (loadedInitialSize < params.key.pageSize) {
            return
        }
        networkState.postValue(NetworkState.LOADING)
        provideObservable(this.params)
                .uiSubscribe(onNext = {
                    if (it.isNotEmpty()) {
                        retry = null
                        this.params.nextPage()
                        callback.onResult(it)
                    }
                    networkState.postValue(NetworkState.LOADED)
                }, onError = {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.ERROR)
                    networkState.postValue(NetworkState.error(it.message ?: "unknown err", it))
                })
    }

    override fun loadInitial(params: LoadInitialParams<Param>, callback: LoadInitialCallback<Item>) {
        this.params.page = 1
        val data: Observable<List<Item>> = provideObservable(this.params)
        //networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            val items = data.blockingFirst()
            retry = null
            if (items.isEmpty()) {
                //networkState.postValue(NetworkState.EMPTY)
                initialLoad.postValue(NetworkState.EMPTY)
            } else {
                //networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
            }
            loadedInitialSize = items.size
            callback.onResult(items)
            this.params.page = this.params.nextPage()
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error", ioException)
            //networkState.postValue(NetworkState.ERROR)
            //initialLoad.postValue(NetworkState.ERROR)
            //networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun getKey(item: Item): Param {
        return params
    }
}
