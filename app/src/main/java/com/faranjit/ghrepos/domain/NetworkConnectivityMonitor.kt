package com.faranjit.ghrepos.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Interface for monitoring network connectivity status.
 * Provides real-time updates about network availability through a [StateFlow].
 */
interface NetworkConnectivityMonitor {

    /**
     * Represents the current network connectivity state.
     * True if the device has an active network connection, false otherwise.
     */
    val isOnline: StateFlow<Boolean>

    /**
     * Starts monitoring network connectivity changes.
     * Should be called when the app starts or becomes active.
     */
    fun startMonitoring()

    /**
     * Stops monitoring network connectivity changes.
     * Should be called when the app is destroyed or goes to background.
     */
    fun stopMonitoring()
}

/**
 * Default implementation of [NetworkConnectivityMonitor] that monitors network connectivity
 * using Android's [ConnectivityManager].
 *
 * Provides real-time network connectivity status updates through a [StateFlow].
 * It registers a network callback to monitor network changes and updates the connection state accordingly.
 *
 * @property context Android context used to access system services
 */
class DefaultNetworkConnectivityMonitor @Inject constructor(
    context: Context
) : NetworkConnectivityMonitor {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnline = MutableStateFlow(true)
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isOnline.value = true
        }

        override fun onLost(network: Network) {
            _isOnline.value = false
        }
    }

    override fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        checkInitialState()
    }

    override fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun checkInitialState() {
        _isOnline.value = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
