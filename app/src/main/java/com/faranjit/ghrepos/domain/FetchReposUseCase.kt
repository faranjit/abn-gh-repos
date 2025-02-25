package com.faranjit.ghrepos.domain

import com.faranjit.ghrepos.data.repository.RepoRepository
import com.faranjit.ghrepos.domain.model.Repo
import com.faranjit.ghrepos.domain.model.toRepoModel
import javax.inject.Inject

/**
 * Use case to fetch the list of repositories by user name.
 *
 * @property repository The repository to fetch the list of repositories.
 */
class FetchReposUseCase @Inject constructor(
    private val repository: RepoRepository
) {

    /**
     * Fetches the list of repositories for the given [username].
     *
     * @param username The username of the user whose repositories are to be fetched.
     * @return The list of repositories.
     */
    suspend operator fun invoke(username: String): List<Repo> =
        repository.getRepos(username).map {
            it.toRepoModel()
        }
}
