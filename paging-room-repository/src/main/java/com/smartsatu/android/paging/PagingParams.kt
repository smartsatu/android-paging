package com.smartsatu.android.paging

open class PagingParams(var page: Int = DEFAULT_PAGE_START, var pageSize: Int = PAGED_LIST_PAGE_SIZE) {

    fun previousPage(): Int {
        page = if (currentPage() > 1) currentPage() - 1 else throw IllegalStateException("Previous page is undefined cause current page = $page")
        return page
    }

    fun currentPage() = page

    fun nextPage(): Int {
        page = currentPage() + 1
        return page
    }

    companion object {

        private const val DEFAULT_PAGE_START = 1

        private const val PAGED_LIST_PAGE_SIZE = 20
    }
}