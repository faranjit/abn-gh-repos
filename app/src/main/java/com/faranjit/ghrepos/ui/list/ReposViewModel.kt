package com.faranjit.ghrepos.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.faranjit.ghrepos.domain.FetchReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    fetchReposUseCase: FetchReposUseCase,
) : ViewModel() {

    val repos = fetchReposUseCase().cachedIn(viewModelScope)
}
