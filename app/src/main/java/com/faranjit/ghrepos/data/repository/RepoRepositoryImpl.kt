package com.faranjit.ghrepos.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.faranjit.ghrepos.data.DefaultRepoPagerFactory
import com.faranjit.ghrepos.data.PagerFactory
import com.faranjit.ghrepos.data.datasource.LocalDataSource
import com.faranjit.ghrepos.data.datasource.RemoteRepoMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Default implementation of [RepoRepository].
 * It fetches the list of repositories from the local database and the network.
 * The network call is made only if the network is available. Otherwise, it returns data from the local database.
 * This class uses a [RemoteRepoMediator] to fetch the data from the network and store it in the local database.
 * To have the list of repositories as a [PagingData], this class uses a [PagerFactory] to create a [Flow] of [PagingData].
 * The [PagingData] is created with a [PagingConfig] that defines the page size.
 * [PagerFactory] creates a [Pager] that fetches the data from the local database and the network.
 *
 * @property localDataSource The local data source to fetch the list of repositories.
 * @property remoteMediator The remote mediator to fetch the list of repositories from the network.
 * @property pagingConfig The paging configuration to be used.
 * @property pagerFactory The factory to create a [Flow] of [PagingData]. @see [DefaultRepoPagerFactory]
 */
@OptIn(ExperimentalPagingApi::class)
class RepoRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteMediator: RemoteRepoMediator,
    private val pagingConfig: PagingConfig,
    private val pagerFactory: PagerFactory<Int, RepoEntity>
) : RepoRepository {

    override fun getRepos(): Flow<PagingData<RepoEntity>> {
        return pagerFactory.createPagerFlow(
            config = pagingConfig,
            remoteMediator = remoteMediator,
            pagingSourceFactory = { localDataSource.getAllRepos() }
        )
    }
}
