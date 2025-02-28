package com.faranjit.ghrepos.data.datasource

import com.faranjit.ghrepos.data.api.GithubApi
import com.faranjit.ghrepos.data.model.OwnerResponse
import com.faranjit.ghrepos.data.model.RepoResponse

class FakeGithubApi(
    private val throwError: Boolean = false,
) : GithubApi {

    override suspend fun getRepos(page: Int, perPage: Int, token: String): List<RepoResponse> {
        if (!throwError) {
            return List(perPage) { index ->
                RepoResponse(
                    repoId = index.toLong(),
                    name = "repo $index",
                    fullName = "faranjit/repo$index",
                    description = "description $index",
                    stars = index,
                    owner = OwnerResponse(
                        id = index.toLong(),
                        login = "faranjit_$index",
                        avatarUrl = "https://https://avatar$index.url.com"
                    ),
                    htmlUrl = "https://github.com/faranjit/repo$index",
                    visibility = "public"
                )
            }
        }

        throw RuntimeException("Error fetching repos")
    }
}
