package com.faranjit.ghrepos

import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import com.faranjit.ghrepos.data.model.OwnerResponse
import com.faranjit.ghrepos.data.model.RepoResponse

fun createRepoEntities(size: Int) = (1..size).map { createRepoEntity(it.toLong()) }

fun createRepoEntity(id: Long) = RepoEntity(
    id = id,
    repoId = id,
    name = "test-repo-$id",
    fullName = "faranjit/test-repo",
    description = "Test repository",
    stars = 10,
    owner = OwnerEntity(
        ownerId = 1L,
        login = "faranjit",
        avatarUrl = "https://avatar.url"
    ),
    htmlUrl = "https://github.com/faranjit/test-repo",
    visibility = "public"
)

fun createRepoResponse(id: Int) = RepoResponse(
    repoId = id.toLong(),
    name = "test-repo-$id",
    fullName = "faranjit/test-repo-$id",
    description = "Test repository $id",
    stars = id * 2,
    htmlUrl = "html $id",
    visibility = if (id % 4 == 0) "private" else "public",
    owner = OwnerResponse(
        id = id.toLong(),
        login = "faranjit",
        avatarUrl = "https://avatar.url"
    )
)
