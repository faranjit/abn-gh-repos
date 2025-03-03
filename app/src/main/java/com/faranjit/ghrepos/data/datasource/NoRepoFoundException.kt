package com.faranjit.ghrepos.data.datasource

/**
 * Exception thrown when no repositories are found.
 *
 * @property networkAvailable Flag to indicate if the network is available.
 */
class NoRepoFoundException(val networkAvailable: Boolean = true) : Throwable()
