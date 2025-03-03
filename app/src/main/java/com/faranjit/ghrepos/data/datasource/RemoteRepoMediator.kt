package com.faranjit.ghrepos.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import com.faranjit.ghrepos.domain.NetworkConnectivityMonitor
import javax.inject.Inject

/**
 * A remote mediator to fetch the list of repositories from the remote data source.
 * This class fetches the list of repositories from the remote data source and stores it in the local database.
 *
 * @property networkMonitor The network monitor to check if the network is available.
 * @property remoteDataSource The remote data source to fetch the list of repositories.
 * @property localDataSource The local data source to store the list of repositories.
 */
@OptIn(ExperimentalPagingApi::class)
class RemoteRepoMediator @Inject constructor(
    private val networkMonitor: NetworkConnectivityMonitor,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : RemoteMediator<Int, RepoEntity>() {

    override suspend fun initialize(): InitializeAction {
        return if (networkMonitor.isOnline.value) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            if (localDataSource.hasAnyRepos()) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> FIRST_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    if (!networkMonitor.isOnline.value) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextKey
                }
            }

            val repos = remoteDataSource.getRepos(page, state.config.pageSize)
            val endOfPaginationReached = repos.size < state.config.pageSize

            localDataSource.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localDataSource.clearRemoteKeys()
                    localDataSource.clearAllRepos()
                }

                val prevKey = if (page == FIRST_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = repos.map { repo ->
                    RemoteKeysEntity(
                        repoId = repo.repoId,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                localDataSource.insertRemoteKeys(keys)
                localDataSource.insertRepos(repos.map { repo ->
                    repo.toEntity()
                })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            // try to get results from db if exists
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, RepoEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo -> localDataSource.getRemoteKeyForRepoId(repo.repoId) }
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}
