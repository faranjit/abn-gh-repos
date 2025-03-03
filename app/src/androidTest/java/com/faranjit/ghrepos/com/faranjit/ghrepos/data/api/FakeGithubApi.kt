package com.faranjit.ghrepos.com.faranjit.ghrepos.data.api

import com.faranjit.ghrepos.createRepoResponse
import com.faranjit.ghrepos.data.api.GithubApi
import com.faranjit.ghrepos.data.model.RepoResponse
import javax.inject.Inject

class FakeGithubApi @Inject constructor() : GithubApi {

    private val repos = List(35) { index ->
        createRepoResponse(index)
    }

    override suspend fun getRepos(page: Int, perPage: Int, token: String?): List<RepoResponse> {
        val start = (page - 1) * 10
        return repos.subList(start, minOf(start + 10, repos.size))
    }
}
