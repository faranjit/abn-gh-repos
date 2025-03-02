package com.faranjit.ghrepos.data.model

import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoResponse(
    @SerialName("id")
    val repoId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("description")
    val description: String?,
    @SerialName("stargazers_count")
    val stars: Int,
    @SerialName("owner")
    val owner: OwnerResponse,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("visibility")
    val visibility: String,
) {

    /**
     * Converts the [RepoResponse] model to [RepoEntity] entity.
     *
     * @return The [RepoEntity] entity.
     */
    fun toEntity(): RepoEntity {
        return RepoEntity(
            repoId = repoId,
            name = name,
            fullName = fullName,
            description = description,
            stars = stars,
            visibility = visibility,
            htmlUrl = htmlUrl,
            owner = owner.toEntity(),
        )
    }
}

@Serializable
data class OwnerResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("login")
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
) {

    /**
     * Converts the [OwnerResponse] model to [OwnerEntity] entity.
     *
     * @return The [OwnerEntity] entity.
     */
    fun toEntity(): OwnerEntity {
        return OwnerEntity(
            ownerId = id,
            login = login,
            avatarUrl = avatarUrl
        )
    }
}
