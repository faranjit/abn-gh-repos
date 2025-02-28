package com.faranjit.ghrepos.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.faranjit.ghrepos.domain.FetchReposUseCase
import com.faranjit.ghrepos.domain.model.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val fetchReposUseCase: FetchReposUseCase,
) : ViewModel() {

    fun fetchRepos(): Flow<PagingData<Repo>> {
        return fetchReposUseCase().cachedIn(viewModelScope)
    }
}
