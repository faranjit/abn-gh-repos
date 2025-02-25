package com.faranjit.ghrepos.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faranjit.ghrepos.domain.FetchReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val fetchReposUseCase: FetchReposUseCase,
) : ViewModel() {

    fun fetchRepos(username: String) {
        viewModelScope.launch {
            fetchReposUseCase(username)
        }
    }
}
