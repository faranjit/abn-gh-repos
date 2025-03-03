package com.faranjit.ghrepos.ui

import androidx.paging.PagingData
import com.faranjit.ghrepos.domain.model.Repo

/**
 * A class that defines UI states
 */
sealed interface ReposUiState {
    data object Loading : ReposUiState
    data class Success(val pagingData: PagingData<Repo>) : ReposUiState
    data class Error(val message: String, val showRetry: Boolean = false) : ReposUiState
    data object ConnectionRestored: ReposUiState
}
