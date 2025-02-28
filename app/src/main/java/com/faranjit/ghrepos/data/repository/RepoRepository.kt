package com.faranjit.ghrepos.data.repository

import androidx.paging.PagingData
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching the list of repositories.
 */
interface RepoRepository {

    /**
     * Fetches the list of repositories for the given username.
     *
     * @return The list of repositories.
     */
    fun getRepos(): Flow<PagingData<RepoEntity>>
}
