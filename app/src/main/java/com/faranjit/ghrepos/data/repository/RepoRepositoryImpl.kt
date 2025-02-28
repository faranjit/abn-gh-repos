package com.faranjit.ghrepos.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.faranjit.ghrepos.ConnectivityChecker
import com.faranjit.ghrepos.data.PagerFactory
import com.faranjit.ghrepos.data.datasource.LocalDataSource
import com.faranjit.ghrepos.data.datasource.RemoteRepoMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RepoRepositoryImpl @Inject constructor(
    private val connectivityChecker: ConnectivityChecker,
    private val localDataSource: LocalDataSource,
    private val remoteMediator: RemoteRepoMediator,
    private val pagingConfig: PagingConfig,
    private val pagerFactory: PagerFactory
) : RepoRepository {

    override fun getRepos(): Flow<PagingData<RepoEntity>> {
        return pagerFactory.createPager(
            config = pagingConfig,
            remoteMediator = if (connectivityChecker.isNetworkAvailable()) remoteMediator else null,
            pagingSourceFactory = { localDataSource.getAllRepos() }
        ).flow
    }
}
