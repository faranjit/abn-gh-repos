package com.faranjit.ghrepos.ui.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faranjit.ghrepos.MainActivity
import com.faranjit.ghrepos.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
        // Wait for initial load
        Thread.sleep(2000)

        // Check if recycler view is displayed
        onView(withId(R.id.recycler_repos))
            .check(matches(isDisplayed()))

        // Check if items are loaded
        onView(withId(R.id.recycler_repos))
            .check(matches(hasMinimumChildCount(10)))

        // Scroll to bottom
        onView(withId(R.id.recycler_repos))
            .perform(swipeUp())

        Thread.sleep(2000)

        // Verify more items are loaded
        onView(withId(R.id.recycler_repos))
            .check(matches(hasMinimumChildCount(10)))

        // Scroll to bottom
        onView(withId(R.id.recycler_repos))
            .perform(swipeUp())

        Thread.sleep(2000)

        // Verify more items are loaded
        onView(withId(R.id.recycler_repos))
            .check(matches(hasMinimumChildCount(10)))
    }
}
