package com.faranjit.ghrepos.domain.model

import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity

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

internal fun RepoEntity.toRepoModel(): Repo = Repo(
    id = repoId,
    name = name,
    fullName = fullName,
    description = description,
    starsCount = stars,
    visibility = visibility,
    htmlUrl = htmlUrl,
    owner = owner.toRepoOwnerModel()
)

internal fun OwnerEntity.toRepoOwnerModel(): RepoOwner =
    RepoOwner(
        id = ownerId,
        login = login,
        avatarUrl = avatarUrl
    )
