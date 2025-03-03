package com.faranjit.ghrepos.ui.list

import androidx.test.espresso.IdlingResource

/**
 * ReposIdlingResource is an implementation of the IdlingResource interface
 * used to synchronize Espresso tests with background operations.
 */
class ReposIdlingResource : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null

    private var isIdle = true

    override fun getName() = "ReposFragment"

    override fun isIdleNow() = isIdle

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    /**
     * Sets the idle state of the resource. If the resource transitions to idle,
     * the registered callback is notified.
     *
     * @param idle indicating whether the resource is idle.
     */
    fun setIdle(idle: Boolean) {
        isIdle = idle
        if (idle) {
            callback?.onTransitionToIdle()
        }
    }
}