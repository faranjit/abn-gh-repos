package com.faranjit.ghrepos.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.faranjit.ghrepos.data.repository.RepoRepository
import com.faranjit.ghrepos.domain.model.Repo
import com.faranjit.ghrepos.domain.model.toRepoModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A use case to fetch the list of repositories by user name.
 * This use case fetches the list of repositories for the 'abnamrocoesd' user.
 * Data is fetched through a repository and the data is transformed to the domain model.
 *
 * @property repository The repository to fetch the list of repositories.
 */
class FetchReposUseCase @Inject constructor(
    private val repository: RepoRepository
) {

    /**
     * Fetches the list of repositories for the 'abnamrocoesd' user.
     *
     * @return The list of repositories.
     */
    operator fun invoke(
    ): Flow<PagingData<Repo>> =
        repository.getRepos().map { pagingData ->
            pagingData.map { it.toRepoModel() }
        }
}
