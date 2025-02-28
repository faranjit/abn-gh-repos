package com.faranjit.ghrepos

class TestConnectivityChecker(
    private val isNetworkAvailable: Boolean,
) : ConnectivityChecker {

    override fun isNetworkAvailable(): Boolean = isNetworkAvailable
}
