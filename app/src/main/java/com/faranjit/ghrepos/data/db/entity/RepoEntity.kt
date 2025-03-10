package com.faranjit.ghrepos.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RepoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val repoId: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val stars: Int,
    val visibility: String,
    @Embedded
    val owner: OwnerEntity,
)

@Entity
data class OwnerEntity(
    val ownerId: Long,
    val login: String,
    val avatarUrl: String,
)
