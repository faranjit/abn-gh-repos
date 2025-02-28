package com.faranjit.ghrepos.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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

@OptIn(ExperimentalPagingApi::class)
class FakeRepoPagerFactory(
    private val data: List<RepoEntity> = emptyList()
) : PagerFactory<Int, RepoEntity> {

    override fun createPagerFlow(
        config: PagingConfig,
        remoteMediator: RemoteMediator<Int, RepoEntity>?,
        pagingSourceFactory: () -> PagingSource<Int, RepoEntity>
    ): Flow<PagingData<RepoEntity>> {
        return flowOf(PagingData.from(data))
    }
}
