package com.faranjit.ghrepos.domain.model

import com.faranjit.ghrepos.data.model.OwnerResponse
import com.faranjit.ghrepos.data.model.RepoResponse

data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val starsCount: Int,
    val visibility: String,
    val htmlUrl: String,
    val owner: RepoOwner,
)

data class RepoOwner(
    val id: Long,
    val login: String,
    val avatarUrl: String,
)

internal fun RepoResponse.toRepoModel(): Repo = Repo(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    starsCount = stars,
    visibility = visibility,
    htmlUrl = htmlUrl,
    owner = owner.toRepoOwnerModel()
)

internal fun OwnerResponse.toRepoOwnerModel(): RepoOwner =
    RepoOwner(
        id = id,
        login = login,
        avatarUrl = avatarUrl
    )
