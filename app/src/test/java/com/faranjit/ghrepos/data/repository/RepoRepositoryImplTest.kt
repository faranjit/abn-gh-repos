package com.faranjit.ghrepos.data.repository

import com.faranjit.ghrepos.data.api.GithubApi
import com.faranjit.ghrepos.data.model.OwnerResponse
import com.faranjit.ghrepos.data.model.RepoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoRepositoryImplTest {

    private val api: GithubApi = mockk()

    private val repository: RepoRepository = RepoRepositoryImpl(api)

    @Test
    fun `getRepos should return repos from api`() = runTest {
        // given
        val username = "faranjit"
        val page = 1
        val size = 20
        val expected = listOf(createRepoResponse())
        coEvery { api.getRepos(username, page, size) } returns expected

        // when
        val actual = repository.getRepos(username, page, size)

        // then
        assertEquals(expected, actual)
        coVerify { api.getRepos(username, page, size) }
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
