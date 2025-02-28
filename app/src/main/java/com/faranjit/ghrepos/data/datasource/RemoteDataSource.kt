package com.faranjit.ghrepos.data.datasource

import com.faranjit.ghrepos.data.api.GithubApi
import com.faranjit.ghrepos.data.model.RepoResponse
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * A data source for fetching the list of repositories from remote by using [Retrofit].
 *
 * @property api The [GithubApi] instance to fetch the repositories.
 */
class RemoteDataSource @Inject constructor(
    private val api: GithubApi
) {

    /**
     * Fetches the list of repositories from GitHubApi.
     *
     * @param page The page index of the results.
     * @param perPage The number of results per page.
     * @return The list of repositories. @see [RepoResponse]
     */
    suspend fun getRepos(page: Int = 1, perPage: Int = 10): List<RepoResponse> =
        api.getRepos(page, perPage)
}