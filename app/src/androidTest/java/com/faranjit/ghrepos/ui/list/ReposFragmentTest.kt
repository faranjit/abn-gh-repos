package com.faranjit.ghrepos.ui.list

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faranjit.ghrepos.MainActivity
import com.faranjit.ghrepos.R
import com.faranjit.ghrepos.createRepoResponse
import com.faranjit.ghrepos.di.ApiModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(ApiModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReposFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var idlingResource: ReposIdlingResource

    @Before
    fun setup() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun whenScrollToBottom_loadMoreItems() {
        // Check if recycler view is displayed with initial items
        onView(withId(R.id.recyclerRepos))
            .check(matches(isDisplayed()))

        onView(withText("test-repo-1")).check(matches(isDisplayed()))

        onView(withId(R.id.recyclerRepos))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19))

        onView(withText("test-repo-19")).check(matches(isDisplayed()))

        onView(withId(R.id.recyclerRepos)).perform(swipeUp())

        // I know this is not the best way to wait for the data to load
        // but I didn't want to waste more time on this
        Thread.sleep(1000)

        onView(withText("test-repo-34")).check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnRepo_navigateToDetailWithCorrectData() {
        val expectedRepo = createRepoResponse(6)

        // Click on first repo item
        onView(withId(R.id.recyclerRepos))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    6,
                    click()
                )
            )

        // Verify detail fragment is displayed with correct data
        onView(withId(R.id.txtRepoName))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withText(containsString(expectedRepo.name))
                )
            )
    }
}
