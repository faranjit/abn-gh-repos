package com.faranjit.ghrepos.data.api

import com.faranjit.ghrepos.data.model.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("users/{username}/repos")
    suspend fun getRepos(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") size: Int = 10
    ): List<RepoResponse>
}
