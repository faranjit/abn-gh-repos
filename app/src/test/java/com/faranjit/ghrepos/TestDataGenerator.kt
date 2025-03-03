package com.faranjit.ghrepos

import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity

fun createRepoEntities(size: Int) = (1..size).map { createRepoEntity(it.toLong()) }

fun createRepoEntity(id: Long) = RepoEntity(
    id = id,
    repoId = id,
    name = "test-repo",
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
