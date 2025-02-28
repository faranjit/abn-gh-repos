package com.faranjit.ghrepos.data.api

import com.faranjit.ghrepos.data.model.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * A Retrofit API interface for fetching the list of repositories from Github.
 */
interface GithubApi {

    @GET("users/abnamrocoesd/repos")
    suspend fun getRepos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Header("Authorization") token: String = "Bearer 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"
    ): List<RepoResponse>
}
