package com.faranjit.ghrepos

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Interface to check the network connectivity.
 */
interface ConnectivityChecker {

    /**
     * Checks if the network is available.
     *
     * @return `true` if the network is available, `false` otherwise.
     */
    fun isNetworkAvailable(): Boolean
}

/**
 * Utility class to check the network connectivity.
 *
 * @property context The application context.
 */
class AndroidConnectivityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityChecker {

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
