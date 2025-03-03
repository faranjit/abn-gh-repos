package com.faranjit.ghrepos.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import com.faranjit.ghrepos.domain.FetchReposUseCase
import com.faranjit.ghrepos.domain.NetworkConnectivityMonitor
import com.faranjit.ghrepos.ui.ReposUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val networkMonitor: NetworkConnectivityMonitor,
    private val fetchReposUseCase: FetchReposUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReposUiState>(ReposUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentJob: Job? = null

    private var lastOnline = true

    init {
        networkMonitor.startMonitoring()

        viewModelScope.launch {
            networkMonitor.isOnline.collectLatest { isOnline ->
                if (isOnline && !lastOnline) {
                    _uiState.value = ReposUiState.ConnectionRestored
                }
                lastOnline = isOnline
            }
        }

        fetchRepos()
    }

    fun fetchRepos() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                fetchReposUseCase()
                    .cachedIn(this)
                    .collect { pagingData ->
                        _uiState.value = ReposUiState.Success(pagingData)
                    }
            } catch (e: Exception) {
                val isNetworkError = e is IOException
                _uiState.value = ReposUiState.Error(
                    message = e.message ?: "Unknown error occurred",
                    showRetry = isNetworkError
                )
            }
        }
    }

    fun handleLoadStates(loadStates: CombinedLoadStates) {
        if (loadStates.refresh is LoadState.Loading && _uiState.value !is ReposUiState.Success) {
            _uiState.value = ReposUiState.Loading
        } else {
            val errorState = loadStates.refresh as? LoadState.Error
                ?: loadStates.append as? LoadState.Error
                ?: loadStates.prepend as? LoadState.Error
            errorState ?: return
            _uiState.value = ReposUiState.Error(
                errorState.error.message ?: "Unknown error occurred!",
                errorState.error is IOException
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
        networkMonitor.stopMonitoring()
    }
}
