package com.faranjit.ghrepos.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RemoteRepoMediator @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : RemoteMediator<Int, RepoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> FIRST_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
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
                localDataSource.insertRepos(repos.mapIndexed { index, repo ->
                    // generate unique id for each repo item to maintain order
                    repo.toEntity((index + (page - 1) * state.config.pageSize).toLong())
                })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
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
