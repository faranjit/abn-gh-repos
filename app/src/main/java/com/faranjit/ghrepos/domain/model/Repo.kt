package com.faranjit.ghrepos.domain.model

import androidx.annotation.DrawableRes
import com.faranjit.ghrepos.R
import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity

data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val starsCount: Int,
    val visibility: RepoVisibility,
    val htmlUrl: String,
    val owner: RepoOwner,
)

data class RepoOwner(
    val id: Long,
    val login: String,
    val avatarUrl: String,
)

enum class RepoVisibility(val visibility: String, @DrawableRes val imageId: Int) {
    PUBLIC("public", R.drawable.ic_visibility_on),
    PRIVATE("private", R.drawable.ic_visibility_off),
}

fun String.toRepoVisibility(): RepoVisibility = when (this) {
    RepoVisibility.PUBLIC.visibility -> RepoVisibility.PUBLIC
    RepoVisibility.PRIVATE.visibility -> RepoVisibility.PRIVATE
    else -> throw IllegalArgumentException("Invalid visibility: $this")
}

internal fun RepoEntity.toRepoModel(): Repo = Repo(
    id = repoId,
    name = name,
    fullName = fullName,
    description = description,
    starsCount = stars,
    visibility = visibility.toRepoVisibility(),
    htmlUrl = htmlUrl,
    owner = owner.toRepoOwnerModel()
)

internal fun OwnerEntity.toRepoOwnerModel(): RepoOwner =
    RepoOwner(
        id = ownerId,
        login = login,
        avatarUrl = avatarUrl
    )
