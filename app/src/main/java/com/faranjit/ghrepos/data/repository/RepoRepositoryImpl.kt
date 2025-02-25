package com.faranjit.ghrepos.data.repository

import com.faranjit.ghrepos.data.api.GithubApi
import com.faranjit.ghrepos.data.model.RepoResponse
import javax.inject.Inject

class RepoRepositoryImpl @Inject constructor(
    private val api: GithubApi
) : RepoRepository {

    override suspend fun getRepos(username: String, page: Int, size: Int): List<RepoResponse> {
        return api.getRepos(username, page, size)
    }
}