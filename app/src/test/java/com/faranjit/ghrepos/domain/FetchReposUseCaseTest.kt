package com.faranjit.ghrepos.domain

import com.faranjit.ghrepos.data.model.OwnerResponse
import com.faranjit.ghrepos.data.model.RepoResponse
import com.faranjit.ghrepos.data.repository.RepoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchReposUseCaseTest {

    private val repository: RepoRepository = mockk()

    private val useCase = FetchReposUseCase(repository)

    @Test
    fun `invoke should return mapped repos from repository`() = runTest {
        // given
        val username = "faranjit"
        val repoResponse = createRepoResponse()
        coEvery { repository.getRepos(username) } returns listOf(repoResponse)

        // when
        val result = useCase(username)

        // then
        assertEquals(1, result.size)
        assertEquals(repoResponse.id, result[0].id)
        assertEquals(repoResponse.name, result[0].name)
        assertEquals(repoResponse.fullName, result[0].fullName)
        assertEquals(repoResponse.description, result[0].description)
        assertEquals(repoResponse.stars, result[0].starsCount)
        assertEquals(repoResponse.owner.id, result[0].owner.id)
        assertEquals(repoResponse.owner.login, result[0].owner.login)
        assertEquals(repoResponse.owner.avatarUrl, result[0].owner.avatarUrl)
        assertEquals(repoResponse.htmlUrl, result[0].htmlUrl)
        assertEquals(repoResponse.visibility, result[0].visibility)
        coVerify { repository.getRepos(username) }
    }

    private fun createRepoResponse() = RepoResponse(
        id = 1L,
        name = "test-repo",
        fullName = "faranjit/test-repo",
        description = "Test repository",
        stars = 10,
        owner = OwnerResponse(
            id = 1L,
            login = "faranjit",
            avatarUrl = "https://avatar.url"
        ),
        htmlUrl = "https://github.com/faranjit/test-repo",
        visibility = "public"
    )
}
