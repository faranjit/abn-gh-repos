package com.faranjit.ghrepos.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoResponse(
    @SerialName("id")
    val id: Long,
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
)

@Serializable
data class OwnerResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("login")
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
)
