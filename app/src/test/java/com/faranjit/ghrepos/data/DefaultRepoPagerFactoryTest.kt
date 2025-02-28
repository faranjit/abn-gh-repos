package com.faranjit.ghrepos.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@OptIn(ExperimentalPagingApi::class)
class DefaultRepoPagerFactoryTest {

    private val factory: PagerFactory<Int, RepoEntity> =
        DefaultRepoPagerFactory()

    @Test
    fun `createPager should create pager with given config and mediator`() {
        // Given
        val config = PagingConfig(10)
        val remoteMediator = mockk<RemoteMediator<Int, RepoEntity>>()
        val pagingSource = mockk<PagingSource<Int, RepoEntity>>()

        // When
        val pagerFlow = factory.createPagerFlow(
            config = config,
            remoteMediator = remoteMediator
        ) { pagingSource }

        // Then
        assertNotNull(pagerFlow)
    }

    @Test
    fun `createPager should create pager without remote mediator`() {
        // Given
        val config = PagingConfig(10)
        val pagingSource = mockk<PagingSource<Int, RepoEntity>>()

        // When
        val pagerFlow = factory.createPagerFlow(
            config = config,
            remoteMediator = null
        ) { pagingSource }

        // Then
        assertNotNull(pagerFlow)
    }

    @Test
    fun `createPager should use provided paging source factory`() = runTest {
        // Given
        val config = PagingConfig(10)
        val pagingSource = mockk<PagingSource<Int, RepoEntity>>().apply {
            every { registerInvalidatedCallback(any()) } returns Unit
            coEvery { load(any()) } returns mockk()
        }
        var factoryCalled = false

        // When
        val pagerFlow = factory.createPagerFlow(
            config = config,
            remoteMediator = null
        ) {
            factoryCalled = true
            pagingSource
        }

        // Then
        assertNotNull(pagerFlow)
        pagerFlow.take(1).collect {}
        assertTrue(factoryCalled)
    }
}