package com.smartsatu.android.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
@Deprecated("Use PagingRoomRepository instead")
abstract class PagingDataSourceFactory<Param : PagingParams, Item, Source : PagingDataSource<Param, Item>>(
        private val params: Param) : DataSource.Factory<Param, Item>() {
    abstract fun createConcreteDataSource(params: Param): Source
    val sourceLiveData = MutableLiveData<Source>()
    override fun create(): Source {
        val source = createConcreteDataSource(params)
        sourceLiveData.postValue(source)
        return source
    }
}
