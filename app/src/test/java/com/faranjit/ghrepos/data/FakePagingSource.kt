package com.faranjit.ghrepos.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

class FakePagingSource<T : Any>(
    private val data: List<T>
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return LoadResult.Page(
            data = data,
            prevKey = null,
            nextKey = null
        )
    }
}
