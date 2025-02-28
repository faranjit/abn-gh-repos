package com.faranjit.ghrepos.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Factory interface to create a [Flow] of [PagingData].
 *
 * @see [Flow]
 * @see [PagingData]
 * @see [PagingConfig]
 * @see [PagingSource]
 * @see [RemoteMediator]
 */
@OptIn(ExperimentalPagingApi::class)
interface PagerFactory<Key : Any, Value : Any> {
    /**
     * Creates a paging flow.
     * @param config The [PagingConfig] to be used.
     * @param remoteMediator The [RemoteMediator] to be used.
     * @param pagingSourceFactory The factory function to create a [PagingSource].
     * @return Flow of [PagingData].
     */
    fun createPagerFlow(
        config: PagingConfig,
        remoteMediator: RemoteMediator<Key, Value>?,
        pagingSourceFactory: () -> PagingSource<Key, Value>
    ): Flow<PagingData<RepoEntity>>
}

/**
 * Default implementation of [PagerFactory].
 * @see [PagerFactory]
 */
@OptIn(ExperimentalPagingApi::class)
class DefaultRepoPagerFactory : PagerFactory<Int, RepoEntity> {

    override fun createPagerFlow(
        config: PagingConfig,
        remoteMediator: RemoteMediator<Int, RepoEntity>?,
        pagingSourceFactory: () -> PagingSource<Int, RepoEntity>
    ): Flow<PagingData<RepoEntity>> {
        return Pager(
            config = config,
            remoteMediator = remoteMediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
