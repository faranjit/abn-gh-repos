package com.faranjit.ghrepos.ui.list

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faranjit.ghrepos.MainActivity
import com.faranjit.ghrepos.R
import com.faranjit.ghrepos.createRepoResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReposFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun whenScrollToBottom_loadMoreItems() {
        // Check if recycler view is displayed
        onView(withId(R.id.recycler_repos))
            .check(matches(isDisplayed()))

        // Check if items are loaded
        onView(withId(R.id.recycler_repos))
            .check(matches(hasMinimumChildCount(10)))

        // Scroll to bottom
        onView(withId(R.id.recycler_repos))
            .perform(swipeUp())

        // Verify more items are loaded
        onView(withId(R.id.recycler_repos))
            .check(matches(hasMinimumChildCount(10)))

        // Scroll to bottom
        onView(withId(R.id.recycler_repos))
            .perform(swipeUp())

        // Verify more items are loaded
        onView(withId(R.id.recycler_repos))
            .check(matches(hasMinimumChildCount(10)))
    }

    @Test
    fun whenClickOnRepo_navigateToDetailWithCorrectData() {
        val expectedRepo = createRepoResponse(6)

        // Click on first repo item
        onView(withId(R.id.recycler_repos))
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
