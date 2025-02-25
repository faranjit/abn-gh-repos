package com.faranjit.ghrepos.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faranjit.ghrepos.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReposFragment : Fragment(R.layout.fragment_repos) {

    private val viewModel: ReposViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchRepos("abnamrocoesd")
    }
}
