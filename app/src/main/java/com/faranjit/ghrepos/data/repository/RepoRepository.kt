package com.faranjit.ghrepos.data.repository

import com.faranjit.ghrepos.data.model.RepoResponse

/**
 * Repository for fetching the list of repositories.
 */
interface RepoRepository {

    /**
     * Fetches the list of repositories for the given [username].
     *
     * @param username The username of the user whose repositories are to be fetched.
     * @param page The page number of the results.
     * @param size The number of results per page.
     * @return The list of repositories.
     */
    suspend fun getRepos(username: String, page: Int = 1, size: Int = 10): List<RepoResponse>
}