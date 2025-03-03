package com.faranjit.ghrepos.data.datasource

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.faranjit.ghrepos.data.db.AppDatabase
import com.faranjit.ghrepos.data.db.RemoteKeysDao
import com.faranjit.ghrepos.data.db.RepoDao
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import javax.inject.Inject

/**
 * Local data source to interact with the database.
 * It provides methods to make database operations over the repositories and remote keys.
 *
 * @property database The [AppDatabase] instance.
 * @property repoDao The [RepoDao] instance.
 * @property remoteKeysDao The [RemoteKeysDao] instance.
 */
class LocalDataSource @Inject constructor(
    private val database: AppDatabase,
    private val repoDao: RepoDao,
    private val remoteKeysDao: RemoteKeysDao
) {

    /**
     * Insert repos into the database.
     *
     * @param repos List of [RepoEntity] to be inserted.
     */
    suspend fun insertRepos(repos: List<RepoEntity>) {
        repoDao.insertRepos(repos)
    }

    /**
     * Checks if there are any repositories stored in the local database.
     *
     * @return True if there is at least one repository in the database, false otherwise.
     */
    suspend fun hasAnyRepos(): Boolean {
        return repoDao.getReposCount() > 0
    }

    /**
     * Get all repos from the database.
     *
     * @return [PagingSource] of [RepoEntity].
     */
    fun getAllRepos(): PagingSource<Int, RepoEntity> {
        return repoDao.getAllRepos()
    }

    /**
     * Clear all repos from the database.
     */
    suspend fun clearAllRepos() {
        repoDao.clearAllRepos()
    }

    /**
     * Clear all remote keys from the database.
     */
    suspend fun clearRemoteKeys() {
        remoteKeysDao.clearRemoteKeys()
    }

    /**
     * Insert remote keys to be used for arranging page numbers of paging into the database.
     *
     * @param keys List of [RemoteKeysEntity] to be inserted.
     */
    suspend fun insertRemoteKeys(keys: List<RemoteKeysEntity>) {
        remoteKeysDao.insertAll(keys)
    }

    /**
     * Get remote key for a repo id.
     *
     * @param repoId Repo id for which remote key is to be fetched.
     * @return [RemoteKeysEntity] for the repo id.
     */
    suspend fun getRemoteKeyForRepoId(repoId: Long): RemoteKeysEntity? {
        return remoteKeysDao.getRemoteKeysForRepoId(repoId)
    }

    /**
     * Starts the specified suspending block as a database transaction.
     */
    suspend fun withTransaction(block: suspend () -> Unit) {
        database.withTransaction(block)
    }
}
