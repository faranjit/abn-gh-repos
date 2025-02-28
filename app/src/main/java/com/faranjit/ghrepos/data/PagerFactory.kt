package com.faranjit.ghrepos.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator

/**
 * Factory interface to create [Pager] instances.
 * @see [PagingData]
 * @see [PagingConfig]
 * @see [PagingSource]
 * @see [RemoteMediator]
 */
@OptIn(ExperimentalPagingApi::class)
interface PagerFactory {
    /**
     * Creates a [Pager] instance.
     * @param config The [PagingConfig] to be used.
     * @param remoteMediator The [RemoteMediator] to be used.
     * @param pagingSourceFactory The factory function to create a [PagingSource].
     * @return The created [Pager] instance.
     */
    fun <Key : Any, Value : Any> createPager(
        config: PagingConfig,
        remoteMediator: RemoteMediator<Key, Value>?,
        pagingSourceFactory: () -> PagingSource<Key, Value>
    ): Pager<Key, Value>
}

/**
 * Default implementation of [PagerFactory].
 * @see [PagerFactory]
 */
@OptIn(ExperimentalPagingApi::class)
class DefaultPagerFactory : PagerFactory {

    override fun <Key : Any, Value : Any> createPager(
        config: PagingConfig,
        remoteMediator: RemoteMediator<Key, Value>?,
        pagingSourceFactory: () -> PagingSource<Key, Value>
    ): Pager<Key, Value> {
        return Pager(
            config = config,
            remoteMediator = remoteMediator,
            pagingSourceFactory = pagingSourceFactory
        )
    }
}
