package com.smartsatu.android.paging

interface PaginationRepository<Param : PagingParams, Item> {

    fun fetchPaging(params: Param): Paging<Item>
}